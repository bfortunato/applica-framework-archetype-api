package applica.api.services.impl;

import applica.api.domain.exceptions.WorkflowException;
import applica.api.domain.model.Filters;
import applica.api.domain.model.dossiers.*;
import applica.api.domain.model.users.Customer;
import applica.api.domain.model.users.Fabricator;
import applica.api.domain.utils.DocumentPriceUtils;
import applica.api.services.DocumentsService;
import applica.api.services.DossiersService;
import applica.api.services.exceptions.CustomerNotFoundException;
import applica.api.services.exceptions.DocumentTypeNotFoundException;
import applica.api.services.exceptions.DossierNotFoundException;
import applica.api.services.exceptions.FabricatorNotFoundException;
import applica.api.services.utils.FileUtils;
import applica.framework.Filter;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.fileserver.FileServer;
import applica.framework.library.options.OptionsManager;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
    private OptionsManager optionsManager;

    @Override
    public List<Dossier> findDossiersByFabricator(Object fabricatorId) {
        return Repo.of(Dossier.class)
                .find(Query.build().filter(Filters.FABRICATOR_ID, fabricatorId, Filter.EQ)
                .sort(Filters.CREATION_DATE, false))
                .getRows()
                .stream()
                .map(this::materializeCustomer)
                .collect(Collectors.toList());

    }



    public Dossier materializeCustomer(Dossier dossier) {
        if (dossier.getCustomerId() != null && dossier.getCustomer() == null) {
            Repo.of(Customer.class).get(dossier.getCustomerId()).ifPresent(dossier::setCustomer);
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
    public List<Document> attachDocument(Object dossierId, Object documentTypeId, byte[] attachmentData, String attachmentName) throws DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        try {
            String extension = FilenameUtils.getExtension(attachmentName);
            String file = fileServer.saveFile("/files/documents", FilenameUtils.getExtension(attachmentName), new ByteArrayInputStream(attachmentData));
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
            DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
            dossierWorkflow.attachDocument(documentTypeId, file, preview);
            saveDossier(dossier);
            documentsService.materializeDocumentTypes(dossier.getDocuments());
            return dossier.getDocuments();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Document> attachDocument(Object dossierId, Object documentTypeId, String path) throws DossierNotFoundException, IOException {
        InputStream is = fileServer.getFile(path);
        File file = new File(path);
        return attachDocument(dossierId, documentTypeId, is.readAllBytes(), file.getName());
    }

    @Override
    public void clearDocumentAttachment(Object dossierId, Object documentTypeId) throws DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.clearDocumentAttachment(documentTypeId);
        saveDossier(dossier);
    }

    @Override
    public List<Document> refuseDocument(Object dossierId, Object documentTypeId, String refuseReason) throws DossierNotFoundException, DocumentTypeNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DocumentType documentType = Repo.of(DocumentType.class).get(documentTypeId).orElseThrow(()->new DocumentTypeNotFoundException(documentTypeId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.refuseDocument(documentTypeId, refuseReason);
        saveDossier(dossier);
        if (Objects.equals(documentType.getAssignationType(), DocumentType.FABRICATOR_PROFILE) || Objects.equals(documentType.getAssignationType(), DocumentType.PREPARATORY_DOCUMENTATION)) {
            //TODO: Mandare mail al serramentista
        }
        documentsService.materializeDocumentTypes(dossier.getDocuments());
        return dossier.getDocuments();
    }

    @Override
    public void saveDossier(Dossier dossier) {
        boolean needToCalculateCost = true;
        if (dossier.getId() == null){
            Dossier old = Repo.of(Dossier.class).find(Query.build().sort(Filters.CODE, true)).findFirst().orElse(null);
            if (old != null) {
                dossier.setCode(old.getCode() + 1);
                if (Objects.equals(dossier.getPriceCalculatorSheet(), old.getPriceCalculatorSheet())){
                    needToCalculateCost = false;
                }
            }
            else
                dossier.setCode(1);
        }
        if (needToCalculateCost) {
            dossier.setServiceCost(calculateServiceCost(dossier.getPriceCalculatorSheet()));
            dossier.setRecommendedPrice(calculateRecommendedPrice(dossier.getPriceCalculatorSheet()));
            dossier.setSimulatedFinancing(simulateFinancing(dossier.getPriceCalculatorSheet()));
        }
        Repo.of(Dossier.class).save(dossier);
    }

    @Override
    public Dossier create(Object fabricatorId, Object customerId, PriceCalculatorSheet priceCalculatorSheet) throws CustomerNotFoundException, FabricatorNotFoundException {
        Fabricator fabricator = Repo.of(Fabricator.class).get(fabricatorId).orElseThrow(() -> new FabricatorNotFoundException(fabricatorId));
        Customer customer = Repo.of(Customer.class).get(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow();
        dossierWorkflow.create(
                fabricator,
                customer,
                priceCalculatorSheet
        );
        Dossier dossier = dossierWorkflow.get();
        dossier.setDocuments(documentsService.generateDossierDocuments());
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
        return dossier;
    }

    @Override
    public Dossier commit(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.commit();
        saveDossier(dossier);
        return materializeCustomer(dossier);
    }

    @Override
    public Dossier candidate(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.candidate();
        saveDossier(dossier);
        return materializeCustomer(dossier);
    }

    @Override
    public Dossier approve(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.approve();
        saveDossier(dossier);
        return materializeCustomer(dossier);
    }

    @Override
    public Dossier refuse(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.refuse();
        saveDossier(dossier);
        return materializeCustomer(dossier);
    }

    @Override
    public Dossier payOff(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.payOff();
        saveDossier(dossier);
        return materializeCustomer(dossier);
    }

    @Override
    public Dossier getById(Object dossierId) throws DossierNotFoundException {
        return Repo.of(Dossier.class).get(dossierId).orElseThrow(()-> new DossierNotFoundException(dossierId));
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
