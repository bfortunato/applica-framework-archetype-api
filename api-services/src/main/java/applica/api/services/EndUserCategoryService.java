package applica.api.services;

import applica.api.domain.model.users.categories.EndUserCategory;

import java.util.List;

public interface EndUserCategoryService {

    List<EndUserCategory> findAllActive();

}
