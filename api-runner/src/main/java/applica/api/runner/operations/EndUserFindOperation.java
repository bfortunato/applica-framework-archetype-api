package applica.api.runner.operations;

import applica.api.domain.model.users.EndUser;
import applica.api.domain.model.usersCategories.EndUserCategory;
import applica.framework.Entity;
import applica.framework.Repo;
import applica.framework.widgets.operations.BaseFindOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

@Component
public class EndUserFindOperation extends BaseFindOperation {

    @Override
    public Class<? extends Entity> getEntityType() {
        return EndUser.class;
    }

    @Override
    public void onSerializeEntity(ObjectNode node, Entity entity) {
        EndUser endUser = ((EndUser) entity);
        node.putPOJO("_category", Repo.of(EndUserCategory.class).get(endUser.getCategoryId()).orElse(null));
    }
}
