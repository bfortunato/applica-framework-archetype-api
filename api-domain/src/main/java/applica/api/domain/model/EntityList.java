package applica.api.domain.model;

import applica.api.domain.model.auth.Role;

/**
 * Created by antoniolovicario on 18/10/17.
 */
public class EntityList {


    // inserire i nomi di tutte le entità
    public static final String USER = "user";
    public static final String ROLE = "role";
    public static final String END_USER = "endUser";
    public static final String FABRICATOR = "fabricator";
    public static final String ADMIN_USER_CATEGORY = "adminUserCategory";
    public static final String END_USER_CATEGORY = "endUserCategory";
    public static final String FABRICATOR_CATEGORY = "fabricatorCategory";
    public static final String ADMIN_USER = "adminUser";
    public static final String CUSTOMER = "customer";
    public static final String DOCUMENT_TYPE = "documentType";
    public static final String DOSSIER = "dossier";


    public static String [] getPermittedEntitiesByRole(String permission) {

        String [] permittedEntities;

        switch (permission) {

            case Role.ADMIN:
                permittedEntities = getAll();
                break;
            default:
                permittedEntities = new String[0];

        }

        return permittedEntities;

    }

    public static String[] getAll() {
        return new String[]{USER, ROLE, END_USER, FABRICATOR, ADMIN_USER_CATEGORY, END_USER, FABRICATOR_CATEGORY, ADMIN_USER, CUSTOMER, DOSSIER};
    }
}
