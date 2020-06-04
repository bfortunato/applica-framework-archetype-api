package applica.api.facade;

import applica.api.domain.data.RolesRepository;
import applica.api.domain.data.UsersRepository;
import applica.api.domain.model.Entities;
import applica.api.domain.model.Filters;
import applica.api.domain.model.auth.AppPermissions;
import applica.api.domain.model.auth.Role;
import applica.api.domain.model.auth.User;
import applica.api.services.authorizations.AuthorizationContexts;
import applica.api.services.authorizations.CrudPermissions;
import applica.framework.Query;
import applica.framework.library.utils.NullableDateConverter;
import applica.framework.library.utils.SystemOptionsUtils;
import applica.framework.security.SecurityUtils;
import applica.framework.security.authorization.Permissions;
import applica.framework.widgets.acl.CrudPermission;
import applica.framework.widgets.acl.CrudSecurityConfigurer;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static applica.framework.security.authorization.BaseAuthorizationService.SUPERUSER_PERMISSION;

@Component
public class SetupFacade {

    private static final String ADMIN_DEFAULT_USERNAME = "admin@applica.guru";


    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired(required = false)
    private GeoFacade geoFacade;

    public void setupApplication() {
        setupRoles();
        setupPermissions();
        if (geoFacade != null && SystemOptionsUtils.isEnabled("setup.import.geoEntities"))
            geoFacade.importGeoEntities();


        User user = usersRepository.find(Query.build().eq(Filters.USER_MAIL, ADMIN_DEFAULT_USERNAME)).findFirst().orElse(null);
        if (user == null) {
            user = new User();
            user.setCode(1);
            String encodedPassword = SecurityUtils.encryptAndGetPassword("applica");
            user.setMail(ADMIN_DEFAULT_USERNAME);
            user.setPassword(encodedPassword);
            user.setCurrentPasswordSetDate(new Date());
            user.setName("admin");
            user.setActive(true);


            Role role = rolesRepository.find(Query.build().filter(Filters.ROLE_NAME, Role.ADMIN)).findFirst().orElse(null);
            if (role != null) {
                user.setRoles(new ArrayList<>());
                user.getRoles().add(role);
            }
            usersRepository.save(user);
        }

        NullableDateConverter dateConverter = new NullableDateConverter();
        dateConverter.setPatterns(new String[]{"dd/MM/yyyy HH:mm", "MM/dd/yyyy HH:mm", "yyyy-MM-dd HH:mm", "dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "HH:mm"});
        ConvertUtils.register(dateConverter, Date.class);

    }


    private void setupRoles() {
        /*
        Crea i diversi ruoli del sistema, se non sono presenti
        */

        for (String roleDescription : Role.getAllRoles()) {
            Role roleToCreate = rolesRepository.find(Query.build().filter(Filters.ROLE_NAME, roleDescription)).findFirst().orElse(null);
            if (roleToCreate == null) {
                roleToCreate = new Role();
                roleToCreate.setRole(roleDescription);
            }
            roleToCreate.setPermissions(getPermissionByRole(roleDescription));
            rolesRepository.save(roleToCreate);
        }
    }

    private List<String> getPermissionByRole(String roleDescription) {
        List<String> permissions = generateEntityPermissionForRole(roleDescription);
        switch (roleDescription) {

            case Role.ADMIN:
                permissions.add(SUPERUSER_PERMISSION);
                permissions.add(AppPermissions.RESET_USER_PASSWORD);
                break;
            default:
                return new ArrayList<>();
        }

        return permissions;
    }

    private List<String> generateEntityPermissionForRole(String roleDescription) {
        List<String> permissions = new ArrayList<>();
        for (String entity : Entities.getPermittedEntitiesByRole(roleDescription)) {
            permissions.addAll(CrudPermissions.staticPermissions(entity));
        }

        return permissions;
    }

    private void setupPermissions() {

        for (String crudEntity : Entities.getAll()) {
            registerPermissions(crudEntity);
        }

        for (String crudEntity : Entities.getAll()) {
            configureCrudSecurityConfigurer(crudEntity, CrudPermissions.staticPermissions(crudEntity));
        }

        Permissions.instance().scan(getClass().getPackage(), AuthorizationContexts.class.getPackage());

        //Setto eventuali permessi
        initializeCustomPermissions();

    }

    private void initializeCustomPermissions() {
        for (String permission : AppPermissions.getAll()) {
            Permissions.instance().registerStatic(permission);
        }
    }

    private void registerPermissions(String crudEntityName) {
        for (String usersPermission : CrudPermissions.staticPermissions(crudEntityName)) {
            Permissions.instance().registerStatic(usersPermission);
        }
    }

    private void configureCrudSecurityConfigurer(String crudEntityName, List<String> crudPermissions) {

        CrudSecurityConfigurer.instance().configure(crudEntityName, CrudPermission.NEW, crudPermissions.stream().filter(c -> c.endsWith("new")).findFirst().get());
        CrudSecurityConfigurer.instance().configure(crudEntityName, CrudPermission.LIST, crudPermissions.stream().filter(c -> c.endsWith("list")).findFirst().get());
        CrudSecurityConfigurer.instance().configure(crudEntityName, CrudPermission.SAVE, crudPermissions.stream().filter(c -> c.endsWith("save")).findFirst().get());
        CrudSecurityConfigurer.instance().configure(crudEntityName, CrudPermission.EDIT, crudPermissions.stream().filter(c -> c.endsWith("edit")).findFirst().get());
        CrudSecurityConfigurer.instance().configure(crudEntityName, CrudPermission.DELETE, crudPermissions.stream().filter(c -> c.endsWith("delete")).findFirst().get());

    }

}
