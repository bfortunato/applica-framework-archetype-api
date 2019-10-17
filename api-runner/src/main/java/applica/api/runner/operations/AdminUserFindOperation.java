package applica.api.runner.operations;

import applica.api.domain.model.Filters;
import applica.api.domain.model.users.AdminUser;
import applica.api.domain.model.users.categories.AdminUserCategory;
import applica.api.domain.utils.FilterUtils;
import applica.framework.Entity;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.Result;
import applica.framework.widgets.operations.BaseFindOperation;
import applica.framework.widgets.operations.OperationException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

@Component
public class AdminUserFindOperation extends BaseFindOperation {

    @Override
    public Class<? extends Entity> getEntityType() {
        return AdminUser.class;
    }

    @Override
    public void onSerializeEntity(ObjectNode node, Entity entity) {
        AdminUser adminUser = ((AdminUser) entity);
        node.putPOJO("_category", Repo.of(AdminUserCategory.class).get(adminUser.getCategoryId()).orElse(null));
    }

    @Override
    protected Result<? extends Entity> fetch(Query query) throws OperationException {
        if (query.hasFilter(Filters.ACTIVE)) {
            FilterUtils.addActiveFilter(query);
        }
        return super.fetch(query);
    }
}
