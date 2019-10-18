package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.domain.model.users.categories.EndUserCategory;
import applica.api.services.EndUserCategoryService;
import applica.framework.Filter;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class EndUserCategoryServiceImpl implements EndUserCategoryService {
    @Override
    public Result<EndUserCategory> findAllActive() {
        return Repo.of(EndUserCategory.class).find(Query.build().eq(Filters.ACTIVE, true));
    }

    @Override
    public Result<EndUserCategory> findActiveByQuery(Query query) {
        if (query == null)
            query = Query.build();
        if (StringUtils.isNotEmpty(query.getKeyword())) {
            query.getFilters().add(new Filter(Filters.DESCRIPTION, query.getKeyword(), Filter.LIKE));
        }
        query.getFilters().add(new Filter(Filters.ACTIVE, true, Filter.EQ));
        return Repo.of(EndUserCategory.class).find(query);
    }
}
