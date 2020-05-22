package applica.api.domain.utils;

import applica.framework.ApplicationContextProvider;
import applica.framework.library.options.OptionsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityUtils {

    public static String encodePassword(String password) {
        OptionsManager manager = ApplicationContextProvider.provide().getBean(OptionsManager.class);
        return encodePassword(manager, password);
    }

    public static String encodePassword(OptionsManager optionsManager, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

}
