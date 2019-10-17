package applica.api.services;

import applica.api.domain.model.users.categories.FabricatorCategory;

import java.util.List;

public interface FabricatorCategoryService {

    List<FabricatorCategory> findAllActive();

}
