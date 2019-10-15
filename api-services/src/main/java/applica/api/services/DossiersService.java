package applica.api.services;

import applica.api.domain.model.dossiers.*;

import java.util.List;

public interface DossiersService {

    List<Dossier> findDossiersByFabricator(Object fabricatorId);

    ServiceCost calculateServiceCost(PriceCalculatorSheet sheet);
    RecommendedPrice calculateRecommendedPrice(PriceCalculatorSheet sheet);
    SimulatedFinancing simulateFinancing(PriceCalculatorSheet sheet, int numberOfQuotes);

    /**
     * Chiamato dall'app quando viene caricato un documento.
     * In questo caso va controllato, se tutti i documenti sono stati inseriti, andare in stato da verificare chiamando il metodo commit()
     * @param dossierId
     * @param documentTypeId
     * @param attachmentData
     */
    void attachDocument(Object dossierId, Object documentTypeId, byte[] attachmentData); //verifica se tutti i documenti attivi sono caricati, dopodiche chiamare commit

    /**
     * Solitamente chiamato da app, per rimuovere un documento gia allegato
     * @param dossierId
     * @param documentTypeId
     * @param attachmentData
     */
    void clearDocumentAttachment(Object dossierId, Object documentTypeId, byte[] attachmentData);


    void refuseDocument(Object dossierId, Object documentTypeId);


    void saveDossier(Dossier dossier);

    /*workflow*/

    /**
     * Dall'app, quando l'utente per la prima volta crea un dossier, che inizialmente è in stato preventivo
     * @param fabricatorId
     * @param customerId
     * @param priceCalculatorSheet
     * @return
     */
    Dossier create(Object fabricatorId, Object customerId, PriceCalculatorSheet priceCalculatorSheet);


    void confirmQuotation(Dossier dossier);

    /**
     * Da chiamare se tutt
     * @param dossier
     */
    void commit(Dossier dossier);
    /*
    altri metodi del workflow
     */


}
