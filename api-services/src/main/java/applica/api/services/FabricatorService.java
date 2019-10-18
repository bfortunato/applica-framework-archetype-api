package applica.api.services;

import applica.api.domain.model.users.Fabricator;
import applica.framework.Query;
import applica.framework.Result;
import applica.framework.widgets.operations.OperationException;

public interface FabricatorService {
    Result<Fabricator> findAllActive();
    Result<Fabricator> findActiveByQuery(Query query);
    void materializeUser(Fabricator fabricator);
    Object getLoggedUserFabricatorId() throws OperationException;
}
