package applica.api.runner.operations;

import applica.api.domain.model.auth.User;
import applica.api.domain.utils.FilterUtils;
import applica.framework.Entity;
import applica.framework.Query;
import applica.framework.Result;
import applica.framework.widgets.operations.BaseFindOperation;
import org.springframework.stereotype.Component;

@Component
public class UserFindOperation extends BaseFindOperation {

    @Override
    public Class<? extends Entity> getEntityType() {
        return User.class;
    }

    @Override
    public Result<? extends Entity> fetch(Query query) {
        FilterUtils.parseDateFilter("registrationDate", query);

        return super.fetch(query);
    }
}
