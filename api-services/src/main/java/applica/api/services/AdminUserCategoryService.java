package applica.api.services;

import applica.api.domain.model.users.categories.AdminUserCategory;
import applica.framework.Query;
import applica.framework.Result;

public interface AdminUserCategoryService {

    Result<AdminUserCategory> findAllActive();
    Result<AdminUserCategory> findActiveByQuery(Query query);

}
