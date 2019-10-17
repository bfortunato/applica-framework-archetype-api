package applica.api.runner.operations;

import applica.api.domain.model.Filters;
import applica.api.domain.model.users.EndUser;
import applica.api.domain.model.users.categories.EndUserCategory;
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

    @Override
    protected Result<? extends Entity> fetch(Query query) throws OperationException {
        if (query.hasFilter(Filters.ACTIVE)) {
            FilterUtils.addActiveFilter(query);
        }
        return super.fetch(query);
    }
}
