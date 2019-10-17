package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.domain.model.users.categories.EndUserCategory;
import applica.api.services.EndUserCategoryService;
import applica.framework.Query;
import applica.framework.Repo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EndUserCategoryServiceImpl implements EndUserCategoryService {
    @Override
    public List<EndUserCategory> findAllActive() {
        return Repo.of(EndUserCategory.class).find(Query.build().eq(Filters.ACTIVE, true)).getRows();
    }
}
