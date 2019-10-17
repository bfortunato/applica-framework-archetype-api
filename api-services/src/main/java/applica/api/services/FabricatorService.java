package applica.api.services;

import applica.api.domain.model.users.Fabricator;
import applica.framework.Result;

public interface FabricatorService {
    Result<Fabricator> findAllActive();
}
