package applica.api.runner.operations;

import applica.api.domain.model.users.Fabricator;
import applica.api.domain.model.usersCategories.FabricatorCategory;
import applica.framework.Entity;
import applica.framework.Repo;
import applica.framework.widgets.operations.BaseFindOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

@Component
public class FabricatorFindOperation extends BaseFindOperation {

    @Override
    public Class<? extends Entity> getEntityType() {
        return Fabricator.class;
    }

    @Override
    public void onSerializeEntity(ObjectNode node, Entity entity) {
        Fabricator fabricator = ((Fabricator) entity);
        node.putPOJO("_category", Repo.of(FabricatorCategory.class).get(fabricator.getCategoryId()).orElse(null));
    }
}
