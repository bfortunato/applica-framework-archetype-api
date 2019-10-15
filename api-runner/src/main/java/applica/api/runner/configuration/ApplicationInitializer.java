package applica.api.runner.configuration;

import applica.api.domain.data.GeoCityRepository;
import applica.api.domain.data.GeoProvinceRepository;
import applica.api.domain.data.RolesRepository;
import applica.api.domain.data.UsersRepository;
import applica.api.domain.model.EntityList;
import applica.api.domain.model.Filters;
import applica.api.domain.model.auth.CustomPermissions;
import applica.api.domain.model.auth.Role;
import applica.api.domain.model.auth.User;
import applica.api.domain.model.geo.GeoCity;
import applica.api.domain.model.geo.GeoProvince;
import applica.api.runner.facade.AccountFacade;
import applica.api.runner.facade.CsvFacade;
import applica.api.runner.permissions.PermissionMap;
import applica.api.services.authorizations.AuthorizationContexts;
import applica.framework.Query;
import applica.framework.fileserver.viewmodel.UIFileUpload;
import applica.framework.library.options.OptionsManager;
import applica.framework.library.utils.NullableDateConverter;
import applica.framework.revision.services.RevisionService;
import applica.framework.security.authorization.Permissions;
import applica.framework.widgets.acl.CrudPermission;
import applica.framework.widgets.acl.CrudSecurityConfigurer;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static applica.framework.security.authorization.BaseAuthorizationService.SUPERUSER_PERMISSION;

/**
 * Created by bimbobruno on 22/11/2016.
 */
@Component
public class ApplicationInitializer {

    private static final String DEFAULT_ADMIN_PASSWORD = "applica";
    private static final String DEFAULT_ADMIN_USERNAME = "admin@applica.guru";

    private Log logger = LogFactory.getLog(getClass());

    @Autowired(required = false)
    private RevisionService revisionService;

    @Autowired
    private OptionsManager options;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private GeoCityRepository geoCityRepository;

    @Autowired
    private GeoProvinceRepository geoProvinceRepository;

    @Autowired
    private AccountFacade accountFacade;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private OptionsManager optionsManager;

    @Autowired
    private CsvFacade csvFacade;

    public void init() throws Exception {
        if (revisionService != null)
            revisionService.disableRevisionForCurrentThread();

        //LicenseManager.instance().setUser(options.get("applica.framework.licensing.user"));
        //LicenseManager.instance().mustBeValid();

        setupRoles();
        setupPermissions();
        initializeCustomPermissions();

        checkGeoProvinceImportStatus();
        checkGeoCityImportStatus();

        User user = usersRepository.find(Query.build().eq(Filters.USER_MAIL, DEFAULT_ADMIN_USERNAME)).findFirst().orElse(null);
        if (user == null) {
            user = new User();
            String encodedPassword = new BCryptPasswordEncoder().encode("applica");
            user.setMail("admin@applica.guru");
            user.setPassword(encodedPassword);
            user.setName("admin");
            user.setActive(true);
            usersRepository.save(user);
        }


        Role role = rolesRepository.find(Query.build().filter(Filters.ROLE_NAME, Role.ADMIN)).findFirst().orElse(null);

        if (role != null) {
            user.setRoles(new ArrayList<>());
            user.getRoles().add(role);
            usersRepository.save(user);
        }

        NullableDateConverter dateConverter = new NullableDateConverter();
        dateConverter.setPatterns(new String[]{"dd/MM/yyyy HH:mm", "MM/dd/yyyy HH:mm", "yyyy-MM-dd HH:mm", "dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "HH:mm"});
        ConvertUtils.register(dateConverter, Date.class);

        logger.info("Applica Framework app started");
        if (revisionService != null)
            revisionService.enableRevisionForCurrentThread();

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
                roleToCreate.setPermissions(getPermissionByRole(roleDescription));
                rolesRepository.save(roleToCreate);
            }
        }
    }

    private void initializeCustomPermissions() {
        for (String permission : CustomPermissions.getAll()) {
            Permissions.instance().registerStatic(permission);
        }
    }

    private List<String> getPermissionByRole(String roleDescription) {
        switch (roleDescription) {
            case Role.ADMIN:
                return Arrays.asList(CustomPermissions.RESET_USER_PASSWORD, SUPERUSER_PERMISSION);
            default:
                return new ArrayList<>();
        }
    }


    private void setupPermissions() {

        for (String crudEntity : EntityList.getAll()) {
            registerPermissions(crudEntity);
        }

        for (String crudEntity : EntityList.getAll()) {
            configureCrudSecurityConfigurer(crudEntity, PermissionMap.staticPermissions(crudEntity));
        }

        for (String permission : applica.api.runner.permissions.Permissions.getAllPermissions()) {
            Permissions.instance().registerStatic(permission);
        }

        Permissions.instance().scan(getClass().getPackage(), AuthorizationContexts.class.getPackage());

    }

    private void registerPermissions(String crudEntityName) {
        for (String usersPermission : PermissionMap.staticPermissions(crudEntityName)) {
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

    private void checkGeoCityImportStatus() throws Exception {
        GeoCity geoCity = geoCityRepository.find(Query.build().filter(Filters.CAP, "75100")).findFirst().orElse(null);
        if (geoCity == null) {

            Resource res = resourceLoader.getResource("classpath:/csv/geoCities.csv");
            UIFileUpload uiFileUpload = new UIFileUpload();

            String path;
            File file = null;
            if (org.springframework.util.StringUtils.hasLength(optionsManager.get("file.getting.mode")) && optionsManager.get("file.getting.mode").equals("jar")){
                file = new File(optionsManager.get("applica.framework.fileserver.basePath") + File.separator + "temp_geoCities.csv");
                FileOutputStream out = new FileOutputStream(file);
                IOUtils.copy(res.getInputStream(), out);
                path = file.getAbsolutePath();
            } else {
                path = res.getFile().getAbsolutePath();
            }

            uiFileUpload.setPath(path);
            csvFacade.importGeoCity(uiFileUpload);

            File finalFile = file;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (finalFile != null && finalFile.exists()) {
                        finalFile.delete();
                    }
                }
            }).start();

        }
    }

    private void checkGeoProvinceImportStatus() throws Exception {
        GeoProvince geoProvince = geoProvinceRepository.find(null).findFirst().orElse(null);
        if (geoProvince == null) {

            Resource res = resourceLoader.getResource("classpath:/csv/geoProvinces.csv");
            UIFileUpload uiFileUpload = new UIFileUpload();

            String path;
            File file = null;
            if (org.springframework.util.StringUtils.hasLength(optionsManager.get("file.getting.mode")) && optionsManager.get("file.getting.mode").equals("jar")){
                file = new File(optionsManager.get("applica.framework.fileserver.basePath")+ File.separator + "temp_geoProvinces.csv");
                FileOutputStream out = new FileOutputStream(file);
                IOUtils.copy(res.getInputStream(), out);
                path = file.getAbsolutePath();
            } else {
                path = res.getFile().getAbsolutePath();
            }

            uiFileUpload.setPath(path);
            csvFacade.importGeoProvince(uiFileUpload);

            File finalFile = file;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (finalFile != null && finalFile.exists()) {
                        finalFile.delete();
                    }
                }
            }).start();

        }
    }

}
