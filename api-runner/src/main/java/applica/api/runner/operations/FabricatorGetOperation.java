package applica.api.runner.operations;

import applica.api.domain.model.users.Fabricator;
import applica.api.domain.model.users.categories.FabricatorCategory;
import applica.api.services.DocumentsService;
import applica.framework.Entity;
import applica.framework.Repo;
import applica.framework.widgets.operations.BaseGetOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class FabricatorGetOperation extends BaseGetOperation {

    @Autowired
    private DocumentsService documentsService;

    @Override
    protected void finishNode(Entity entity, ObjectNode node) {

        Fabricator fabricator = ((Fabricator) entity);
        node.putPOJO("_category", Repo.of(FabricatorCategory.class).get(fabricator.getCategoryId()).orElse(null));

        node.put("_country", fabricator.getAddress().getCountry());
        node.put("_region", fabricator.getAddress().getRegion());
        node.put("_province", fabricator.getAddress().getProvince());
        node.put("_municipality", fabricator.getAddress().getMunicipality());
        node.put("_postalCode", fabricator.getAddress().getPostalCode());
        node.put("_address", fabricator.getAddress().getAddress());
        node.put("_streetNumber", fabricator.getAddress().getStreetNumber());
        documentsService.materializeDocumentTypes(fabricator.getDocuments());
        node.putPOJO("documents", fabricator.getDocuments());

    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return Fabricator.class;
    }


}
