package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.domain.model.auth.User;
import applica.api.domain.model.users.Fabricator;
import applica.api.services.FabricatorService;
import applica.api.services.responses.ResponseCode;
import applica.framework.Filter;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.Result;
import applica.framework.security.Security;
import applica.framework.widgets.operations.OperationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class FabricatorServiceImpl implements FabricatorService {
    @Override
    public Result<Fabricator> findAllActive() {
        return Repo.of(Fabricator.class).find(Query.build().eq(Filters.ACTIVE, true));
    }

    @Override
    public Result<Fabricator> findActiveByQuery(Query query) {
        if (query == null)
            query = Query.build();
        if (StringUtils.isNotEmpty(query.getKeyword())) {
            query.getFilters().add(new Filter(Filters.BUSINESS_NAME, query.getKeyword(), Filter.LIKE));
        }
        query.getFilters().add(new Filter(Filters.ACTIVE, true, Filter.EQ));
        return Repo.of(Fabricator.class).find(query);
    }

    @Override
    public void materializeUser(Fabricator fabricator) {
        fabricator.setUser(Repo.of(User.class).get(fabricator.getUserId()).orElse(null));
    }

    @Override
    public Object getLoggedUserFabricatorId() throws OperationException {
        User user = (User) Security.withMe().getLoggedUser();
        Fabricator fabricator = Repo.of(Fabricator.class).find(Query.build().eq(Filters.USER_ID, user.getSid())).findFirst().orElseThrow(()-> new OperationException(ResponseCode.ERROR_FABRICATOR_NOT_FOUND));
        return fabricator.getId();
    }
}
