package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.domain.model.users.categories.AdminUserCategory;
import applica.api.services.AdminUserCategoryService;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.Result;
import org.springframework.stereotype.Service;

@Service
public class AdminUserCategoryServiceImpl implements AdminUserCategoryService {
    @Override
    public Result<AdminUserCategory> findAllActive() {
        return Repo.of(AdminUserCategory.class).find(Query.build().eq(Filters.ACTIVE, true));
    }
}
