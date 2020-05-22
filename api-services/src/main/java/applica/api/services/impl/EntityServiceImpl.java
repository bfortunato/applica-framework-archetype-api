package applica.api.services.impl;

import applica.api.services.EntityService;
import applica.framework.Entity;
import applica.framework.Query;
import applica.framework.Repo;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class EntityServiceImpl implements EntityService {

    @Override
    public boolean isUnique(Class<? extends Entity> entityClass, String fieldName, Object fieldValue, Entity entity) {
        Entity duplicated = Repo.of(entityClass).find(Query.build().eq(fieldName, fieldValue)).findFirst().orElse(null);
        return duplicated == null || Objects.equals(duplicated.getId(), entity.getId());
    }
}
