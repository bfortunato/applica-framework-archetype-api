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
import applica.api.services.exceptions.DocumentNotFoundException;
import applica.api.services.exceptions.DossierNotFoundException;
import applica.api.services.exceptions.FabricatorNotFoundException;
import applica.framework.Filter;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.fileserver.FileServer;
import applica.framework.library.options.OptionsManager;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

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
        return Repo.of(Dossier.class).find(Query.build().filter(Filters.FABRICATOR_ID, fabricatorId, Filter.EQ).sort(Filters.CREATION_DATE, false)).getRows();
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
            String file = fileServer.saveFile("/files/documents", FilenameUtils.getExtension(attachmentName), new ByteArrayInputStream(attachmentData));
            String fileserverPath = optionsManager.get("applica.framework.fileserver.basePath") + "\\";
            XWPFDocument wordDocument = new XWPFDocument(new FileInputStream(fileserverPath + file));
            POIXMLProperties props = wordDocument.getProperties();

            String thumbnail = props.getThumbnailFilename();
            String preview = null;
            if (thumbnail == null) {
                // No thumbnail
            } else {
                preview = fileServer.saveFile("/", ", ", props.getThumbnailImage());
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
    public void clearDocumentAttachment(Object dossierId, Object documentTypeId) throws DossierNotFoundException, DocumentNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        boolean removed = dossierWorkflow.clearDocumentAttachment(documentTypeId);
        if (removed)
            saveDossier(dossier);
        else
            throw new DocumentNotFoundException(documentTypeId);
    }

    @Override
    public void refuseDocument(Object dossierId, Object documentTypeId) throws DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.refuseDocument(documentTypeId);
        saveDossier(dossier);
    }

    @Override
    public void saveDossier(Dossier dossier) {
        if (dossier.getId() == null){
            Dossier old = Repo.of(Dossier.class).find(Query.build().sort(Filters.CODE, true)).findFirst().orElse(null);
            if (old != null)
                dossier.setCode(old.getCode() + 1);
            else
                dossier.setCode(1);
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
        return dossier;
    }

    @Override
    public void confirmQuotation(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.confirmQuotation();
        saveDossier(dossier);
    }

    @Override
    public void commit(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.commit();
        saveDossier(dossier);
    }

    @Override
    public void candidate(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.candidate();
        saveDossier(dossier);
    }

    @Override
    public void approve(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.approve();
        saveDossier(dossier);
    }

    @Override
    public void refuse(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.refuse();
        saveDossier(dossier);
    }

    @Override
    public void payOff(Object dossierId) throws WorkflowException, DossierNotFoundException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new DossierNotFoundException(dossierId));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.payOff();
        saveDossier(dossier);
    }

    @Override
    public Dossier getById(Object dossierId) throws DossierNotFoundException {
        return Repo.of(Dossier.class).get(dossierId).orElseThrow(()-> new DossierNotFoundException(dossierId));
    }
}
