package applica.api.runner.operations;

import applica.api.domain.model.users.Fabricator;
import applica.api.domain.model.users.categories.FabricatorCategory;
import applica.framework.Entity;
import applica.framework.Repo;
import applica.framework.widgets.operations.BaseGetOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class FabricatorGetOperation extends BaseGetOperation {

    @Override
    protected void finishNode(Entity entity, ObjectNode node) {

        Fabricator fabricator = ((Fabricator) entity);
        node.putPOJO("_category", Repo.of(FabricatorCategory.class).get(fabricator.getCategoryId()).orElse(null));

        if (fabricator.getAddress() != null){
            node.put("_address", fabricator.getAddress().getAddress());
            node.put("_streetNumber", fabricator.getAddress().getStreetNumber());
            node.putPOJO("_municipality", fabricator.getAddress().getMunicipality());
        }
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return Fabricator.class;
    }


}
