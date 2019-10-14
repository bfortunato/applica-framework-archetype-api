package applica.api.services;

import applica.api.domain.model.dossiers.*;

import java.util.List;

public interface DossiersService {

    List<Dossier> findDossiersByFabricator(Object fabricatorId);
    ServiceCost calculateServiceCost(PriceCalculatorSheet sheet);
    RecommendedPrice calculateRecommendedPrice(PriceCalculatorSheet sheet);
    SimulatedFinancing simulateFinancing(PriceCalculatorSheet sheet, int numberOfQuotes);
    void createDossier(Object fabricatorId);

}
