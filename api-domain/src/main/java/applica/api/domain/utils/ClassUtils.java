package applica.api.domain.utils;

import applica.api.domain.model.Filters;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ClassUtils {

    public interface ClassUtilsRunnable {
        void perform(Class component);
    }



    public static void performForAllSubclassesInModel(Class father, ClassUtilsRunnable runnable) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(father));

        Set<BeanDefinition> components = provider.findCandidateComponents(Filters.class.getPackage().getName());
        for (BeanDefinition component : components)
        {
            Class cls = null;
            try {
                cls = Class.forName(component.getBeanClassName());
                // use class cls found
                runnable.perform(cls);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    public static HashMap<String, List<String>> getAllClassFields (Class c) {
        HashMap<String, List<String>> map = new HashMap<>();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(c.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        List<String> fieldsNameAndType = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
                fieldsNameAndType.add(field.getName() + " (" + field.getType().getSimpleName() + ")");
            }
        }
        map.put(c.getSimpleName(), fieldsNameAndType);
        return map;
    }

    public static HashMap<Object, Object> getAllValuesInClass(Object object) throws IllegalAccessException {
        HashMap<Object, Object> map = new HashMap<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = field.get(object);
            map.put(name, value);
        }
        return map;
    }

}
