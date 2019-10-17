package applica.api.runner.operations;

import applica.api.domain.model.users.AdminUser;
import applica.api.domain.model.users.categories.AdminUserCategory;
import applica.framework.Entity;
import applica.framework.Repo;
import applica.framework.widgets.operations.BaseGetOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class AdminUserGetOperation extends BaseGetOperation {

    @Override
    protected void finishNode(Entity entity, ObjectNode node) {
        node.putPOJO("_category", Repo.of(AdminUserCategory.class).get(((AdminUser) entity).getCategoryId()).orElse(null));
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return AdminUser.class;
    }


}
