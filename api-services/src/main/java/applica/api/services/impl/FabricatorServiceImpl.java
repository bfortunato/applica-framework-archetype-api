package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.domain.model.users.Fabricator;
import applica.api.services.FabricatorService;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.Result;
import org.springframework.stereotype.Service;

@Service
public class FabricatorServiceImpl implements FabricatorService {
    @Override
    public Result<Fabricator> findAllActive() {
        return Repo.of(Fabricator.class).find(Query.build().eq(Filters.ACTIVE, true));
    }
}
