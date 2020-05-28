package applica.api.runner.utils;

import applica.api.domain.model.Filters;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClassUtils {

    public interface ClassUtilsRunnable {
        void perform(Class component);
    }

    public static List<Field> getAllFields(Class<? extends Object> type) {
        ArrayList fields = new ArrayList();
        for(Class c = type; c != null; c = c.getSuperclass()) {
            for (Field declaredField : c.getDeclaredFields()) {
                declaredField.setAccessible(true);
                fields.add(declaredField);
            }
        }

        return fields;
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


}
