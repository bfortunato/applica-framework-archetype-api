package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.services.utils.RepositoryUtils;
import applica.framework.Entity;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.security.EntityService;
import org.apache.commons.beanutils.PropertyUtils;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class EntityServiceImpl implements EntityService {

    @Override
    public boolean isUnique(Class<? extends Entity> entityClass, String fieldName, Object fieldValue, Entity entity) {
        Entity duplicated = Repo.of(entityClass).find(Query.build().eq(fieldName, fieldValue != null? fieldValue: getPropertyWrapper(entity, fieldName))).findFirst().orElse(null);
        return duplicated == null || Objects.equals(duplicated.getId(), entity.getId());
    }

    private Class getPropertyType(Object bean, String property) {
        try {
            return PropertyUtils.getPropertyType(bean, property);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
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
    public void materializePropertyFromId(List<? extends Entity> rows, String idProperty, String entityProperty, Class entityPropertyClass) {
        if (rows != null && rows.size() > 0) {
            try {
                List types = Repo.of(entityPropertyClass)
                        .find(
                                Query.build()
                                        .in(Filters.REPOSITORY_ID, rows.stream()
                                                .filter(d -> getPropertyWrapper(d, idProperty) != null)
                                                .map(d -> {
                                                            List<Object> ids = new ArrayList<>();
                                                            Class propertyClass = getPropertyType(d, idProperty);
                                                            if (List.class.isAssignableFrom(propertyClass)) {
                                                                ids.addAll(((List) getPropertyWrapper(d, idProperty)));
                                                            } else
                                                                ids.add(getPropertyWrapper(d, idProperty));

                                                            return ids.stream().map(id -> new ObjectId(id.toString())).collect(Collectors.toList());
                                                        }

                                                )
                                                .flatMap(Collection::stream)
                                                .distinct().collect(Collectors.toList())))
                        .getRows();
                rows.forEach(d -> setPropertyWrapper(d, entityProperty, types.stream().filter(t -> Objects.equals(((Entity) t).getId(), getPropertyWrapper(d, idProperty))).findFirst().orElse(null)));

            } catch (Exception e) {

            }

        }
    }

    public List<? extends Entity> getEntitiesFromIds(Class<? extends Entity> entityClass, List<String> ids) {
        if (ids == null || ids.size() == 0)
            return new ArrayList<>();
        return Repo.of(entityClass).find(Query.build().in(Filters.REPOSITORY_ID, RepositoryUtils.getRepositoryIdFromIds(ids))).getRows();
    }
}
