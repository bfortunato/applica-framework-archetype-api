package applica.api.services;

import applica.api.domain.exceptions.WorkflowException;
import applica.api.domain.model.dossiers.*;
import applica.api.services.exceptions.*;

import java.io.IOException;
import java.util.List;

public interface DossiersService {

    List<Dossier> findDossiersByFabricator(Object fabricatorId);
    List<Dossier> findDossiersByCustomer(Object fabricatorId);

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
    List<Document> attachDocument(Object dossierId, Object documentTypeId, byte[] attachmentData, String attachmentName) throws DossierNotFoundException; //verifica se tutti i documenti attivi sono caricati, dopodiche chiamare commit

    List<Document> attachDocument(Object dossierId, Object documentTypeId, String path) throws DossierNotFoundException, IOException; //verifica se tutti i documenti attivi sono caricati, dopodiche chiamare commit

    /**
     * Solitamente chiamato da app, per rimuovere un documento gia allegato
     * @param dossierId
     * @param documentTypeId
     */
    List<Document> clearDocumentAttachment(Object dossierId, Object documentTypeId) throws DossierNotFoundException, DocumentNotFoundException;


    List<Document> refuseDocument(Object dossierId, Object documentTypeId, String refuseReason) throws DossierNotFoundException, DocumentTypeNotFoundException;


    void saveDossier(Dossier dossier);

    /*workflow*/

    /**
     * Dall'app, quando l'utente per la prima volta crea un dossier, che inizialmente è in stato preventivo
     * @param fabricatorId
     * @param customerId
     * @param priceCalculatorSheet
     * @return
     */
    Dossier create(Object fabricatorId, Object customerId, PriceCalculatorSheet priceCalculatorSheet, String notes) throws WorkflowException, CustomerNotFoundException, FabricatorNotFoundException;

    Dossier edit(String dossierId, String fabricatorId, String customerId, PriceCalculatorSheet priceCalculatorSheet, String notes) throws FabricatorNotFoundException, CustomerNotFoundException, DossierNotFoundException;

    Dossier quotation(Object dossierId) throws WorkflowException, DossierNotFoundException;

    Dossier confirmQuotation(Object dossierId) throws WorkflowException, DossierNotFoundException;

    /**
     * Da chiamare se tutt
     * @param dossierId
     */
    Dossier commit(Object dossierId) throws WorkflowException, DossierNotFoundException;
    /*
    altri metodi del workflow
     */

    Dossier candidate(Object dossierId) throws WorkflowException, DossierNotFoundException;

    Dossier approve(Object dossierId) throws WorkflowException, DossierNotFoundException;

    Dossier refuse(Object dossierId) throws WorkflowException, DossierNotFoundException;

    Dossier payOff(Object dossierId) throws WorkflowException, DossierNotFoundException;

    Dossier getById(Object dossierId) throws DossierNotFoundException;

    Dossier materializeCustomer(Dossier dossier);
}
