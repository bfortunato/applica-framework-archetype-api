package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.domain.model.auth.User;
import applica.api.domain.model.users.Fabricator;
import applica.api.services.FabricatorService;
import applica.api.services.responses.ResponseCode;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.Result;
import applica.framework.security.Security;
import applica.framework.widgets.operations.OperationException;
import org.springframework.stereotype.Service;

@Service
public class FabricatorServiceImpl implements FabricatorService {
    @Override
    public Result<Fabricator> findAllActive() {
        return Repo.of(Fabricator.class).find(Query.build().eq(Filters.ACTIVE, true));
    }

    @Override
    public Object getLoggedUserFabricatorId() throws OperationException {
        User user = (User) Security.withMe().getLoggedUser();
        Fabricator fabricator = Repo.of(Fabricator.class).find(Query.build().eq(Filters.USER_ID, user.getSid())).findFirst().orElseThrow(()-> new OperationException(ResponseCode.ERROR_FABRICATOR_NOT_FOUND));
        return fabricator.getId();
    }
}
