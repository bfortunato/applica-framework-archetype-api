package applica.api.domain.utils;

import applica.framework.ApplicationContextProvider;
import applica.framework.library.options.OptionsManager;

public class SecurityUtils {

    public static String encodePassword(String password) {
        OptionsManager manager = ApplicationContextProvider.provide().getBean(OptionsManager.class);
        return encodePassword(manager, password);
    }

    public static String encodePassword(OptionsManager optionsManager, String password) {
        password = password + "{" + optionsManager.get("password.salt") + "}";

        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] array = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100), 1, 3);
            }

            return sb.toString();

        } catch (java.security.NoSuchAlgorithmException ignored) {

        }
        return "";
    }

}
