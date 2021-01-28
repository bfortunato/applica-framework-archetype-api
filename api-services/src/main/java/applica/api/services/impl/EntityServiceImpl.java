package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.services.utils.RepositoryUtils;
import applica.framework.ApplicationContextProvider;
import applica.framework.Entity;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.library.options.OptionsManager;
import applica.framework.security.CodeGeneratorService;
import applica.framework.security.EntityService;
import applica.framework.widgets.factory.OperationsFactory;
import org.apache.commons.beanutils.PropertyUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class EntityServiceImpl implements EntityService {

    @Autowired
    private OptionsManager optionsManager;

    @Autowired
    private OperationsFactory operationsFactory;


    @Override
    public boolean isUnique(Class<? extends Entity> entityClass, String fieldName, Object fieldValue, Entity entity) {
        Object propertyValue = fieldValue != null? fieldValue: getPropertyWrapper(entity, fieldName);
        if (propertyValue == null || !StringUtils.hasLength(propertyValue.toString()))
            return true;
        Entity duplicated = Repo.of(entityClass).find(Query.build().eq(fieldName, propertyValue)).findFirst().orElse(null);
        return duplicated == null || Objects.equals(duplicated.getId(), entity.getId());
    }


    private Object getPropertyWrapper(Object bean, String property) {
        try {
            return PropertyUtils.getProperty(bean, property);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setPropertyWrapper(Object bean, String property, Object value) {
        try {
            PropertyUtils.setProperty(bean, property, value);
        } catch (Exception e) {

        }
    }

    @Override
    public void materializePropertyFromId(List<? extends Entity> rows, String idProperty, String entityProperty, Class entityPropertyClass) {
        if (rows != null && rows.size() > 0) {
            try {
                List<ObjectId> objectIds = rows.stream()
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
                        .distinct().collect(Collectors.toList());
                List types = Repo.of(entityPropertyClass)
                        .find(
                                Query.build()
                                        .in(Filters.REPOSITORY_ID, objectIds))
                        .getRows();
                rows.forEach(d -> setPropertyWrapper(d, entityProperty, getPropertyValue(types, d, idProperty)));

            } catch (Exception e) {

            }

        }
    }


    private Object getPropertyValue(List types, Entity d, String idProperty) {
        try {
            Class propertyClass = getPropertyType(d, idProperty);
            if (List.class.isAssignableFrom(propertyClass)) {
                List property = (List) getPropertyWrapper(d, idProperty);
                return types.stream().filter(t -> property != null && property.contains(((Entity) t).getId())).collect(Collectors.toList());

            } else
                return types.stream().filter(t -> Objects.equals(((Entity) t).getId(), getPropertyWrapper(d, idProperty))).findFirst().orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public List<? extends Entity> getEntitiesFromIds(Class<? extends Entity> entityClass, List<String> ids) {
        if (ids == null || ids.size() == 0)
            return new ArrayList<>();
        return Repo.of(entityClass).find(Query.build().in(Filters.REPOSITORY_ID, RepositoryUtils.getRepositoryIdFromIds(ids))).getRows();
    }

    private Class getPropertyType(Object bean, String property) {
        try {
            return PropertyUtils.getPropertyType(bean, property);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}


