package applica.api.services.impl;

import applica.api.domain.exceptions.WorkflowException;
import applica.api.domain.model.Filters;
import applica.api.domain.model.dossiers.*;
import applica.api.domain.model.users.Customer;
import applica.api.domain.model.users.Fabricator;
import applica.api.domain.utils.DocumentPriceUtils;
import applica.api.services.DocumentsService;
import applica.api.services.DossiersService;
import applica.api.services.responses.ResponseCode;
import applica.framework.Filter;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.fileserver.FileServer;
import applica.framework.widgets.operations.OperationException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class DossierServiceImpl implements DossiersService {

    @Autowired
    private FileServer fileServer;

    @Autowired
    private DocumentsService documentsService;

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
    public void attachDocument(Object dossierId, Object documentTypeId, byte[] attachmentData, String attachmentName) throws OperationException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new OperationException(ResponseCode.ERROR_DOSSIER_NOT_FOUND));
        try {
            String file = fileServer.saveFile("/files/documents", FilenameUtils.getExtension(attachmentName), new ByteArrayInputStream(attachmentData));
            Document document = new Document(documentTypeId);
            document.setFile(file);
            document.setValid(true);
            document.setUploadDate(new Date());
            DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
            dossierWorkflow.attachDocument(document);
            saveDossier(dossier);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearDocumentAttachment(Object dossierId, Object documentTypeId) throws OperationException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new OperationException(ResponseCode.ERROR_DOSSIER_NOT_FOUND));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        boolean removed = dossierWorkflow.clearDocumentAttachment(documentTypeId);
        if (removed)
            saveDossier(dossier);
        else
            throw new OperationException(ResponseCode.ERROR_DOCUMENT_NOT_FOUND);
    }

    @Override
    public void refuseDocument(Object dossierId, Object documentTypeId) throws OperationException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new OperationException(ResponseCode.ERROR_DOSSIER_NOT_FOUND));
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
    public Dossier create(Object fabricatorId, Object customerId, PriceCalculatorSheet priceCalculatorSheet) throws OperationException, WorkflowException {
        Fabricator fabricator = Repo.of(Fabricator.class).get(fabricatorId).orElseThrow(() -> new OperationException(ResponseCode.ERROR_FABRICATOR_NOT_FOUND));
        Customer customer = Repo.of(Customer.class).get(customerId).orElseThrow(() -> new OperationException(ResponseCode.ERROR_CUSTOMER_NOT_FOUND));
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
    public void confirmQuotation(Object dossierId) throws WorkflowException, OperationException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new OperationException(ResponseCode.ERROR_DOSSIER_NOT_FOUND));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.confirmQuotation();
        saveDossier(dossier);
    }

    @Override
    public void commit(Object dossierId) throws WorkflowException, OperationException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new OperationException(ResponseCode.ERROR_DOSSIER_NOT_FOUND));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.commit();
        saveDossier(dossier);
    }

    @Override
    public void candidate(Object dossierId) throws WorkflowException, OperationException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new OperationException(ResponseCode.ERROR_DOSSIER_NOT_FOUND));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.candidate();
        saveDossier(dossier);
    }

    @Override
    public void approve(Object dossierId) throws WorkflowException, OperationException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new OperationException(ResponseCode.ERROR_DOSSIER_NOT_FOUND));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.approve();
        saveDossier(dossier);
    }

    @Override
    public void refuse(Object dossierId) throws WorkflowException, OperationException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new OperationException(ResponseCode.ERROR_DOSSIER_NOT_FOUND));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.refuse();
        saveDossier(dossier);
    }

    @Override
    public void payOff(Object dossierId) throws WorkflowException, OperationException {
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(() -> new OperationException(ResponseCode.ERROR_DOSSIER_NOT_FOUND));
        DossierWorkflow dossierWorkflow = new DossierWorkflow(dossier);
        dossierWorkflow.payOff();
        saveDossier(dossier);
    }

    @Override
    public Dossier getById(Object dossierId) throws OperationException {
        return Repo.of(Dossier.class).get(dossierId).orElseThrow(()-> new OperationException(ResponseCode.ERROR_DOSSIER_NOT_FOUND));
    }
}
