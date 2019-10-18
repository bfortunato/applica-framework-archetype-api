package applica.api.services;

import applica.api.domain.model.users.Fabricator;
import applica.framework.Result;
import applica.framework.widgets.operations.OperationException;

public interface FabricatorService {
    Result<Fabricator> findAllActive();

    Object getLoggedUserFabricatorId() throws OperationException;
}
