package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.domain.model.users.categories.FabricatorCategory;
import applica.api.services.FabricatorCategoryService;
import applica.framework.Filter;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class FabricatorCategoryServiceImpl implements FabricatorCategoryService {
    @Override
    public Result<FabricatorCategory> findAllActive() {
        return Repo.of(FabricatorCategory.class).find(Query.build().eq(Filters.ACTIVE, true));
    }

    @Override
    public Result<FabricatorCategory> findActiveByQuery(Query query) {
        if (query == null)
            query = Query.build();
        if (StringUtils.isNotEmpty(query.getKeyword())) {
            query.getFilters().add(new Filter(Filters.DESCRIPTION, query.getKeyword(), Filter.LIKE));
        }
        query.getFilters().add(new Filter(Filters.ACTIVE, true, Filter.EQ));
        return Repo.of(FabricatorCategory.class).find(query);
    }
}
