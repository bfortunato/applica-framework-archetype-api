package applica.api.runner.operations;

import applica.api.domain.model.dossiers.Dossier;
import applica.api.domain.model.users.Customer;
import applica.api.domain.model.users.Fabricator;
import applica.api.services.DocumentsService;
import applica.framework.Entity;
import applica.framework.Repo;
import applica.framework.widgets.operations.BaseGetOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class DossierGetOperation extends BaseGetOperation {

    @Autowired
    private DocumentsService documentsService;

    @Override
    protected void finishNode(Entity entity, ObjectNode node) {
        Dossier dossier = ((Dossier) entity);
        if (dossier.getId() == null){
            dossier.setCreationDate(new Date());
        }
        node.putPOJO("_customer", Repo.of(Customer.class).get(dossier.getCustomerId()).orElse(null));
        node.putPOJO("_fabricator", Repo.of(Fabricator.class).get(dossier.getFabricatorId()).orElse(null));
//        node.putPOJO("_serviceCost", DocumentPriceUtils.generateServiceCost(dossier.getPriceCalculatorSheet()));
//        node.putPOJO("_recommendedPrice", DocumentPriceUtils.generateRecommendedPrice(dossier.getPriceCalculatorSheet()));
        node.put("_significantValue", dossier.getPriceCalculatorSheet().getSignificantValue());
        node.put("_nonSignificantValue", dossier.getPriceCalculatorSheet().getNonSignificantValue());
        node.put("_serviceValue", dossier.getPriceCalculatorSheet().getServiceValue());
        documentsService.materializeDocumentTypes(dossier.getDocuments());
        node.putPOJO("documents", dossier.getDocuments());
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return Dossier.class;
    }


}
