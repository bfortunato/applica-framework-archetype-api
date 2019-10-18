package applica.api.services;

import applica.api.domain.model.users.categories.EndUserCategory;
import applica.framework.Query;
import applica.framework.Result;

public interface EndUserCategoryService {

    Result<EndUserCategory> findAllActive();
    Result<EndUserCategory> findActiveByQuery(Query query);

}
