package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.framework.Entity;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.security.EntityService;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;


@Service
public class EntityServiceImpl implements EntityService {


    @Override
    public boolean isUnique(Class<? extends Entity> entityClass, String fieldName, Object fieldValue, Entity entity) {
        Entity duplicated = Repo.of(entityClass).find(Query.build().eq(fieldName, fieldValue)).findFirst().orElse(null);
        return duplicated == null || Objects.equals(duplicated.getId(), entity.getId());
    }



    private Object getPropertyWrapper(Object bean, String property) {
        try {
            return PropertyUtils.getProperty(bean, property);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }

    private void setPropertyWrapper(Object bean, String property, Object value) {
        try {
            PropertyUtils.setProperty(bean, property, value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {

        }
    }

    @Override
    public void materializePropertyFromId(List<Entity> rows, String idProperty, String entityProperty, Class entityPropertyClass) {
        if (rows != null && rows.size() > 0) {
            try {
                List types = Repo.of(entityPropertyClass).find(Query.build().in(Filters.REPOSITORY_ID, rows.stream().filter(d -> getPropertyWrapper(d, idProperty) != null).map(d -> new ObjectId(getPropertyWrapper(d, idProperty).toString())).distinct().collect(Collectors.toList()))).getRows();
                rows.forEach(d -> setPropertyWrapper(d, entityProperty, types.stream().filter(t -> Objects.equals(((Entity) t).getId(), getPropertyWrapper(d, idProperty))).findFirst().orElse(null)));

            } catch (Exception e) {

            }

        }
    }

}
