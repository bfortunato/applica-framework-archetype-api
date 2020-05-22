package applica.api.services;

import applica.framework.Entity;

public interface EntityService {
    boolean isUnique(Class<? extends Entity> entityClass, String fieldName, Object fieldValue, Entity entity);
}
