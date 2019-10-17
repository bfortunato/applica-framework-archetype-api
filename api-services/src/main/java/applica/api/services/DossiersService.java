package applica.api.services;

import applica.api.domain.exceptions.WorkflowException;
import applica.api.domain.model.dossiers.*;
import applica.framework.widgets.operations.OperationException;

import java.util.List;

public interface DossiersService {

    List<Dossier> findDossiersByFabricator(Object fabricatorId);

    ServiceCost calculateServiceCost(PriceCalculatorSheet sheet);
    RecommendedPrice calculateRecommendedPrice(PriceCalculatorSheet sheet);
    SimulatedFinancing simulateFinancing(PriceCalculatorSheet sheet);

    /**
     * Chiamato dall'app quando viene caricato un documento.
     * In questo caso va controllato, se tutti i documenti sono stati inseriti, andare in stato da verificare chiamando il metodo commit()
     * @param dossierId
     * @param documentTypeId
     * @param attachmentData
     */
    void attachDocument(Object dossierId, Object documentTypeId, byte[] attachmentData, String attachmentName) throws OperationException; //verifica se tutti i documenti attivi sono caricati, dopodiche chiamare commit

    /**
     * Solitamente chiamato da app, per rimuovere un documento gia allegato
     * @param dossierId
     * @param documentTypeId
     */
    void clearDocumentAttachment(Object dossierId, Object documentTypeId) throws OperationException;


    void refuseDocument(Object dossierId, Object documentTypeId) throws OperationException;


    void saveDossier(Dossier dossier);

    /*workflow*/

    /**
     * Dall'app, quando l'utente per la prima volta crea un dossier, che inizialmente è in stato preventivo
     * @param fabricatorId
     * @param customerId
     * @param priceCalculatorSheet
     * @return
     */
    Dossier create(Object fabricatorId, Object customerId, PriceCalculatorSheet priceCalculatorSheet) throws OperationException;


    void confirmQuotation(Object dossierId) throws OperationException, WorkflowException;

    /**
     * Da chiamare se tutt
     * @param dossierId
     */
    void commit(Object dossierId) throws OperationException, WorkflowException;
    /*
    altri metodi del workflow
     */

    void candidate(Object dossierId) throws OperationException, WorkflowException;

    void approve(Object dossierId) throws OperationException, WorkflowException;

    void refuse(Object dossierId) throws OperationException, WorkflowException;

    void payOff(Object dossierId) throws OperationException, WorkflowException;

    Dossier getById(Object dossierId) throws OperationException;
}
