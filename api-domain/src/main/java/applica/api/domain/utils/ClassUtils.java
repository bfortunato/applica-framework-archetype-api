package applica.api.domain.utils;

import applica.api.domain.model.Filters;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

        getAllSubclasses(father).forEach(c -> runnable.perform(c));
    }

    public static List<Class> getAllSubclasses(Class father) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(father));

        Set<BeanDefinition> components = provider.findCandidateComponents(Filters.class.getPackage().getName());


        List<Class> classes = new ArrayList<>();
        components.forEach(c -> {
            try {
                classes.add(Class.forName(c.getBeanClassName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        return classes;
    }


    public static Object invokeMethod(Class clazz, String methodName, Object... params) {
        // String.class here is the parameter type, that might not be the case with you
        try {
            Method method = clazz.getMethod(methodName, String.class);
            return method.invoke(null, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
