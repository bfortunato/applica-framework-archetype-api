package applica.api.runner.permissions;



import applica.api.domain.model.auth.Role;

import java.util.*;

public class Permissions {
    public static final String ADMIN = "permission:admin";

    public static List<String> getPermissionByRole(String role) {
        switch (role) {
            case Role.ADMIN:
                return Arrays.asList(getAllPermissions());
        }
        return new ArrayList<>();
    }

    public static String[] getAllPermissions(){
        return new String[]{ADMIN};
    }
}


