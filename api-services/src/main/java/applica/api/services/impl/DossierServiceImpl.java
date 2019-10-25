package applica.api.services.impl;

import applica.api.domain.exceptions.WorkflowException;
import applica.api.domain.model.Filters;
import applica.api.domain.model.dossiers.*;
import applica.api.domain.model.users.Customer;
import applica.api.domain.model.users.Fabricator;
import applica.api.domain.utils.DocumentPriceUtils;
import applica.api.services.DocumentsService;
import applica.api.services.DossiersService;
import applica.api.services.FabricatorService;
import applica.api.services.exceptions.CustomerNotFoundException;
import applica.api.services.exceptions.DocumentTypeNotFoundException;
import applica.api.services.exceptions.DossierNotFoundException;
import applica.api.services.exceptions.FabricatorNotFoundException;
import applica.api.services.utils.FileUtils;
import applica.framework.Filter;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.fileserver.FileServer;
import applica.framework.security.Security;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

@Service
public class DossierServiceImpl implements DossiersService {

    @Autowired
    private FileServer fileServer;

    @Autowired
    private DocumentsService documentsService;

    @Autowired
    private FabricatorService fabricatorService;

    @Override
    public List<Dossier> findDossiersByFabricator(Object fabricatorId) {
        return Repo.of(Dossier.class)
                .find(Query.build().filter(Filters.FABRICATOR_ID, fabricatorId, Filter.EQ)
                .sort(Filters.CREATION_DATE, false))
                .getRows()
                .stream()
                .map(this::materializeCustomer)
                .map(this::materializeDocumentWithDocumentTypes)
                .collect(Collectors.toList());

    }

    @Override
    public List<Dossier> findDossiersByCustomer(Object customerId) {
        return Repo.of(Dossier.class)
                .find(Query.build().filter(Filters.CUSTOMER_ID, customerId, Filter.EQ)
                        .sort(Filters.CREATION_DATE, false))
                .getRows()
                .stream()
                .map(this::materializeCustomer)
                .map(this::materializeDocumentWithDocumentTypes)
                .collect(Collectors.toList());

    }

    public Dossier materializeCustomer(Dossier dossier) {
        if (dossier.getCustomerId() != null && dossier.getCustomer() == null) {
            Repo.of(Customer.class).get(dossier.getCustomerId()).ifPresent(dossier::setCustomer);
        }

        return dossier;
    }

    @Override
    public Dossier materializeFabricator(Dossier dossier) {
        if (dossier.getFabricatorId() != null && dossier.getFabricator() == null) {
            Repo.of(Fabricator.class).get(dossier.getFabricatorId()).ifPresent(dossier::setFabricator);
        }

        return dossier;
    }

    public Dossier materializeDocumentWithDocumentTypes(Dossier dossier) {
        for (Document d: dossier.getDocuments()) {
            if (d.getDocumentType() == null & d.getDocumentTypeId() != null) {
                Repo.of(DocumentType.class).get(d.getDocumentTypeId()).ifPresent(d::setDocumentType);
            }
        }

        return dossier;
    }

    @Override
    public ServiceCost calculateServiceCost(PriceCalculatorSheet sheet) {
        return DocumentPriceUtils.generateServiceCost(sheet);
    }

    @Override
    public RecommendedPrice calculateRecommendedPrice(PriceCalculatorSheet sheet) {
        return DocumentPriceUtils.generateRecommendedPrice(sheet);
    }

    @Override
    public SimulatedFinancing simulateFinancing(PriceCalculatorSheet sheet) {
        return DocumentPriceUtils.generateSimulatedFinancing(sheet);
    }

    @Override
    public Dossier attachDocumentData(Object dossierId, Object documentTypeId, String base64Data, String attachmentName) throws DossierNotFoundException, DocumentTypeNotFoundException {
        byte[] attachmentData = Base64.getDecoder().decode(base64Data);
        return attachDocumentData(dossierId, documentTypeId, attachmentData, attachmentName);
    }

    @Override
    public Dossier attachDocumentData(Object dossierId, Object documentTypeId, byte[] attachmentData, String attachmentName) throws DossierNotFoundException, DocumentTypeNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DocumentType documentType = Repo.of(DocumentType.class).get(documentTypeId).orElseThrow(() -> new DocumentTypeNotFoundException(documentTypeId));
        try {
            String extension = FilenameUtils.getExtension(attachmentName);
            String file = fileServer.saveFile("/files/documents", FilenameUtils.getExtension(documentType.toPdfName()), new ByteArrayInputStream(attachmentData));
            String preview = null;
            if (haveToGeneratePreview(extension)) {
                File convertedPdfFile = null;

                try {
                    InputStream fileStream = fileServer.getFile(file);
                    if (extension.equals("doc") || extension.equals("docx")) {
                        //trasformo il doc/x in pdf ed ottengo l'anterpima come per il caso pdf
                        convertedPdfFile = createVoidTempFile();
                        FileUtils.convertToPDF(new ByteArrayInputStream(attachmentData), convertedPdfFile);
                        fileStream = new FileInputStream(convertedPdfFile);
                    }
                    preview = createThumbnailFromPdf(fileStream, "/files/documents");

                } catch (Exception e ) {
                    e.printStackTrace();
                }

                File finalConvertedPdfFile = convertedPdfFile;
                new Thread(()-> {
                    if (finalConvertedPdfFile != null)
                        finalConvertedPdfFile.delete();
                }).start();

            } else if (isImage(extension)){
                preview = file;
            }
            documentsService.materializeDocumentTypes(dossier.getDocuments());
            DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
            dossierWorkflow.attachDocument(documentTypeId, file, preview != null ? FileUtils.getImageFullUrl(preview.replaceFirst("/", "")) : null);
            saveDossier(dossier);

            materializeCustomer(dossier);
            materializeDocumentWithDocumentTypes(dossier);

            return dossier;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Dossier attachDocument(Object dossierId, Object documentTypeId, String path) throws DossierNotFoundException, IOException, DocumentTypeNotFoundException {
        InputStream is = fileServer.getFile(path);
        File file = new File(path);
        return attachDocumentData(dossierId, documentTypeId, is.readAllBytes(), file.getName());
    }

    @Override
    public List<Document> clearDocumentAttachment(Object dossierId, Object documentTypeId) throws DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.clearDocumentAttachment(documentTypeId);
        saveDossier(dossier);
        documentsService.materializeDocumentTypes(dossier.getDocuments());
        return dossier.getDocuments();
    }

    @Override
    public List<Document> refuseDocument(Object dossierId, Object documentTypeId, String refuseReason) throws DossierNotFoundException, DocumentTypeNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DocumentType documentType = Repo.of(DocumentType.class).get(documentTypeId).orElseThrow(()->new DocumentTypeNotFoundException(documentTypeId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.refuseDocument(documentTypeId, refuseReason);
        saveDossier(dossier);
        if (Objects.equals(documentType.getAssignationType(), DocumentType.FABRICATOR_PROFILE) || Objects.equals(documentType.getAssignationType(), DocumentType.PREPARATORY_DOCUMENTATION)) {
            fabricatorService.sendDocumentRefusedMail(dossier, documentType);
            fabricatorService.sendDossierDocumentRefusedNotification(dossier, Security.withMe().getLoggedUser().getId());
        }
        documentsService.materializeDocumentTypes(dossier.getDocuments());
        return dossier.getDocuments();
    }

    @Override
    public void saveDossier(Dossier dossier) {
        boolean needToCalculateCost = true;
        if (dossier.getId() == null){
            Dossier previous = Repo.of(Dossier.class).find(Query.build().sort(Filters.CODE, true)).findFirst().orElse(null);
            if (previous != null) {
                dossier.setCode(previous.getCode() + 1);
            }
            else
                dossier.setCode(1);
        } else {
            Dossier old = Repo.of(Dossier.class).get(dossier.getId()).orElse(null);
            if (old != null){
                if (Objects.equals(dossier.getPriceCalculatorSheet().getSignificantValue(), old.getPriceCalculatorSheet().getSignificantValue())
                        && Objects.equals(dossier.getPriceCalculatorSheet().getNonSignificantValue(), old.getPriceCalculatorSheet().getNonSignificantValue())
                        && Objects.equals(dossier.getPriceCalculatorSheet().getServiceValue(), old.getPriceCalculatorSheet().getServiceValue())){
                    needToCalculateCost = false;
                }
            }
        }
        if (needToCalculateCost) {
            dossier.setServiceCost(calculateServiceCost(dossier.getPriceCalculatorSheet()));
            dossier.setRecommendedPrice(calculateRecommendedPrice(dossier.getPriceCalculatorSheet()));
            dossier.setSimulatedFinancing(simulateFinancing(dossier.getPriceCalculatorSheet()));
        }
        Repo.of(Dossier.class).save(dossier);
    }

    @Override
    public Dossier create(Object fabricatorId, Object customerId, PriceCalculatorSheet priceCalculatorSheet, String notes, boolean serviceFeeInvoiced) throws CustomerNotFoundException, FabricatorNotFoundException {
        Fabricator fabricator = Repo.of(Fabricator.class).get(fabricatorId).orElseThrow(() -> new FabricatorNotFoundException(fabricatorId));
        Customer customer = Repo.of(Customer.class).get(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow();
        dossierWorkflow.create(
                fabricator,
                customer,
                priceCalculatorSheet,
                notes,
                serviceFeeInvoiced
        );
        Dossier dossier = dossierWorkflow.get();
        dossier.setDocuments(documentsService.generateDossierDocuments());
        saveDossier(dossier);
        dossier.setCustomer(customer);
        return dossier;
    }

    @Override
    public Dossier edit(String dossierId, String fabricatorId, String customerId, PriceCalculatorSheet priceCalculatorSheet, String notes, boolean serviceFeeInvoiced) throws FabricatorNotFoundException, CustomerNotFoundException, DossierNotFoundException {
        Fabricator fabricator = Repo.of(Fabricator.class).get(fabricatorId).orElseThrow(() -> new FabricatorNotFoundException(fabricatorId));
        Customer customer = Repo.of(Customer.class).get(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.edit(fabricator, customer, priceCalculatorSheet, notes, serviceFeeInvoiced);
        saveDossier(dossier);
        dossier.setCustomer(customer);
        materializeDocumentWithDocumentTypes(dossier);
        return dossier;
    }
    @Override
    public Dossier quotation(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.quotation();
        saveDossier(dossier);
        materializeCustomer(dossier);
        return dossier;
    }

    @Override
    public Dossier confirmQuotation(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.confirmQuotation();
        saveDossier(dossier);
        materializeCustomer(dossier);
        materializeDocumentWithDocumentTypes(dossier);
        return dossier;
    }

    @Override
    public Dossier commit(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.commit();
        saveDossier(dossier);
        materializeCustomer(dossier);
        materializeDocumentWithDocumentTypes(dossier);

        return dossier;
    }

    @Override
    public Dossier candidate(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.candidate();
        saveDossier(dossier);
        materializeCustomer(dossier);
        materializeDocumentWithDocumentTypes(dossier);

        return dossier;
    }

    @Override
    public Dossier approve(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.approve();
        saveDossier(dossier);
        materializeCustomer(dossier);
        materializeDocumentWithDocumentTypes(dossier);

        return dossier;
    }

    @Override
    public Dossier refuse(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.refuse();
        saveDossier(dossier);
        materializeCustomer(dossier);
        materializeDocumentWithDocumentTypes(dossier);

        return dossier;
    }

    @Override
    public Dossier payOff(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.payOff();
        saveDossier(dossier);
        materializeCustomer(dossier);
        materializeDocumentWithDocumentTypes(dossier);

        return dossier;
    }

    @Override
    public Dossier getById(Object dossierId) throws DossierNotFoundException {
        return Repo.of(Dossier.class).get(dossierId).orElseThrow(()-> new DossierNotFoundException(dossierId));
    }

    @Override
    public DossierCount count() {
        DossierCount count = new DossierCount();

        List<Dossier> dossiers = Repo.of(Dossier.class).find(null).getRows();

        count.setAll(dossiers.size());
        count.setQuotation(dossiers.stream().filter(d->Objects.equals(d.getStatus(), Dossier.STATUS_QUOTATION)).count());
        count.setDraft(dossiers.stream().filter(d->Objects.equals(d.getStatus(), Dossier.STATUS_DRAFT)).count());
        count.setTocandidate(dossiers.stream().filter(d->Objects.equals(d.getStatus(), Dossier.STATUS_TO_CANDIDATE)).count());
        count.setCandidated(dossiers.stream().filter(d->Objects.equals(d.getStatus(), Dossier.STATUS_CANDIDATED)).count());
        count.setApproved(dossiers.stream().filter(d->Objects.equals(d.getStatus(), Dossier.STATUS_APPROVED)).count());
        count.setPayoff(dossiers.stream().filter(d->Objects.equals(d.getStatus(), Dossier.STATUS_PAY_OFF)).count());
        count.setRefused(dossiers.stream().filter(d->Objects.equals(d.getStatus(), Dossier.STATUS_REFUSED)).count());

        return count;
    }

    @Override
    public void delete(String dossierId) throws DossierNotFoundException, IOException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        for (Document d: dossier.getDocuments()
             ) {
            if (StringUtils.hasLength(d.getFile()))
                fileServer.deleteFile(d.getFile());
            if (StringUtils.hasLength(d.getPreview()))
                fileServer.deleteFile(d.getPreview());
        }
        Repo.of(Dossier.class).delete(dossierId);
    }

    private boolean haveToGeneratePreview(String extension) {
        return extension.equals("pdf") || extension.equals("doc") || extension.equals("docx");
    }

    public String createThumbnailFromPdf(InputStream i, String fileServerPath) throws Exception {
        //lo salvo in una directory temporanea
        String absolutePath = createTempFile(i);

        //lo carico nell'oggetto PDDocument
        PDDocument document = loadPdfDocumentFromFile(absolutePath);

        //acquisisco la prima pagina del documento e creo una BufferedImage
        java.util.List pages = document.getDocumentCatalog().getAllPages();
        PDPage page = (PDPage) pages.get(0);
        BufferedImage image = page.convertToImage();
        File outputfile = new File(absolutePath + "preview" +"_"+ 0 +".png");
        System.out.println("Image Created -> " + outputfile.getName());
        ImageIO.write(image, "jpg", outputfile);


        InputStream is = new FileInputStream(outputfile);

        //con l'inputstream salvo nel fileserver l'anteprima e mi ritorno il suo path
        return fileServer.saveFile(fileServerPath, "jpg", is);

    }


    public String createTempFile(InputStream i) throws Exception {

        File dir = createVoidTempFile();

        copyInputStreamToFile(i, dir);

        return dir.getAbsolutePath();

    }

    private File createVoidTempFile() throws Exception {

        return File.createTempFile("temp", "");
    }

    private static PDDocument loadPdfDocumentFromFile(String fileName) throws IOException {
        return PDDocument.load(fileName);
    }

    public boolean isImage(String extension) {
        return extension.toLowerCase().equals("png") || extension.toLowerCase().equals("jpg") || extension.toLowerCase().equals("jpeg") || extension.toLowerCase().equals("bmp");
    }
}
