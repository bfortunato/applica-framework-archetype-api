package applica.api.runner.operations;

import applica.api.domain.exceptions.WorkflowException;
import applica.api.domain.model.dossiers.Dossier;
import applica.api.domain.model.dossiers.DossierWorkflow;
import applica.api.domain.model.dossiers.PriceCalculatorSheet;
import applica.api.domain.model.users.Customer;
import applica.api.domain.model.users.Fabricator;
import applica.framework.Entity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        DossierWorkflow dossierWorkflow = new DossierWorkflow();

        PriceCalculatorSheet priceCalculatorSheet = null;

        if (node.get("_significantValue") != null && node.get("_nonSignificantValue") != null && node.get("_serviceValue") != null) {
            priceCalculatorSheet = new PriceCalculatorSheet(node.get("_significantValue").asDouble(), node.get("_nonSignificantValue").asDouble(), node.get("_serviceValue").asDouble());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Fabricator fabricator = null;
        try {
            fabricator = objectMapper.treeToValue(node.get("_fabricator"), Fabricator.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Customer customer = null;
        try {
            customer = objectMapper.treeToValue(node.get("_customer"), Customer.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            dossierWorkflow.create(fabricator, customer, priceCalculatorSheet);
            Dossier d = dossierWorkflow.get();
            dossier.setCreationDate(d.getCreationDate());
            dossier.setFabricatorId(d.getFabricatorId());
            dossier.setCustomerId(d.getCustomerId());
            dossier.setStatus(d.getStatus());
            dossier.setPriceCalculatorSheet(d.getPriceCalculatorSheet());
            dossier.setDocuments(d.getDocuments());
        } catch (WorkflowException e) {
            e.printStackTrace();
        }

    }

}
