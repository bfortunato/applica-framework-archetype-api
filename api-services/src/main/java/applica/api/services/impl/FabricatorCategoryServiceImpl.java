package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.domain.model.users.categories.FabricatorCategory;
import applica.api.services.FabricatorCategoryService;
import applica.framework.Query;
import applica.framework.Repo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FabricatorCategoryServiceImpl implements FabricatorCategoryService {
    @Override
    public List<FabricatorCategory> findAllActive() {
        return Repo.of(FabricatorCategory.class).find(Query.build().eq(Filters.ACTIVE, true)).getRows();
    }
}
