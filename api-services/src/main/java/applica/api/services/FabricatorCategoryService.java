package applica.api.services;

import applica.api.domain.model.users.categories.FabricatorCategory;
import applica.framework.Query;
import applica.framework.Result;

public interface FabricatorCategoryService {

    Result<FabricatorCategory> findAllActive();
    Result<FabricatorCategory> findActiveByQuery(Query query);

}
