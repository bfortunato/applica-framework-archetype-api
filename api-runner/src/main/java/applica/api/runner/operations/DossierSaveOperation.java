package applica.api.runner.operations;

import applica.api.domain.model.dossiers.Dossier;
import applica.framework.Entity;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class DossierSaveOperation extends EntityCodedBaseSaveOperation {

    @Override
    public Class<? extends Entity> getEntityType() {
        return Dossier.class;
    }

    @Override
    protected void finishEntity(ObjectNode node, Entity entity) {

        Dossier dossier = ((Dossier) entity);

        if (node.get("_customer") != null){
            dossier.setCustomerId(node.get("_customer").get("id").asText());
        }

        if (node.get("_fabricator") != null){
            dossier.setFabricatorId(node.get("_fabricator").get("id").asText());
        }

        if (node.get("_significantValue") != null) {
            dossier.getPriceCalculatorSheet().setSignificantValue(node.get("_significantValue").asDouble());
        }

        if (node.get("_nonSignificantValue") != null) {
            dossier.getPriceCalculatorSheet().setNonSignificantValue(node.get("_nonSignificantValue").asDouble());
        }

        if (node.get("_serviceValue") != null) {
            dossier.getPriceCalculatorSheet().setServiceValue(node.get("_serviceValue").asDouble());
        }
    }

}
