package applica.api.runner.configuration;

import applica.api.domain.data.RolesRepository;
import applica.api.domain.data.UsersRepository;
import applica.api.domain.model.Filters;
import applica.api.domain.model.auth.AppPermissions;
import applica.api.domain.model.auth.Role;
import applica.api.domain.model.auth.User;
import applica.api.services.authorizations.AuthorizationContexts;
import applica.api.services.authorizations.CrudPermissions;
import applica.framework.Query;
import applica.framework.library.utils.NullableDateConverter;
import applica.framework.revision.services.RevisionService;
import applica.framework.security.authorization.Permissions;
import applica.framework.widgets.acl.CrudPermission;
import applica.framework.widgets.acl.CrudSecurityConfigurer;
import applica.framework.widgets.entities.EntitiesRegistry;
import applica.framework.widgets.entities.EntityDefinition;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

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
    private UsersRepository usersRepository;

    @Autowired
    private RolesRepository rolesRepository;

    public void init() {
        if (revisionService != null)
            revisionService.disableRevisionForCurrentThread();

        Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http"));

        for(String log:loggers) {
            Logger logger = (Logger)LoggerFactory.getLogger(log);
            logger.setLevel(Level.ERROR);
            logger.setAdditive(false);
        }

        //LicenseManager.instance().setUser(options.get("applica.framework.licensing.user"));
        //LicenseManager.instance().mustBeValid();


        // e in italiano: 20 Febbraio 2014
        Locale.setDefault(Locale.ITALIAN);

        setupPermissions();
        setupRoles();
        initializeCustomPermissions();

        createUserIfNotExists(DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD,  "admin", Role.ADMIN);

        NullableDateConverter dateConverter = new NullableDateConverter();
        dateConverter.setPatterns(new String[]{"dd/MM/yyyy HH:mm", "MM/dd/yyyy HH:mm", "yyyy-MM-dd HH:mm", "dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "HH:mm"});
        ConvertUtils.register(dateConverter, Date.class);

        logger.info("Applica Framework app started");
        if (revisionService != null)
            revisionService.enableRevisionForCurrentThread();


        //influxService.buildTimeseries();

    }

    private User createUserIfNotExists(String mail, String adminPassword, String name, String role) {
        User user = usersRepository.find(Query.build().eq(Filters.USER_MAIL, mail)).findFirst().orElse(null);
        if (user == null) {
            user = new User();
            String encodedPassword = new BCryptPasswordEncoder().encode(adminPassword);
            user.setMail(mail);
            user.setPassword(encodedPassword);
            user.setName(name);
            user.setActive(true);
            user.setRoles(Collections.singletonList(getRole(role)));
            usersRepository.save(user);
        }

        return user;
    }


    private void setupRoles() {
        /*
        Crea i diversi ruoli del sistema, se non sono presenti
        */
        for (String roleDescription : Role.getAllRoles()) {
            Role roleToCreate = getRole(roleDescription);
            if (roleToCreate == null) {
                roleToCreate = new Role();
                roleToCreate.setRole(roleDescription);
                roleToCreate.getPermissions().addAll(AppPermissions.getPermissionsByRole(roleDescription));
                roleToCreate.getPermissions().addAll(CrudPermissions.getEntityPermissionsByRole(roleDescription));
                rolesRepository.save(roleToCreate);
            }
        }
    }

    private Role getRole(String roleDescription) {
        return rolesRepository.find(Query.build().filter(Filters.ROLE_NAME, roleDescription)).findFirst().orElse(null);
    }

    private void initializeCustomPermissions() {
        for (String permission : AppPermissions.getAll()) {
            Permissions.instance().registerStatic(permission);
        }
    }



    private void setupPermissions() {
        EntitiesRegistry.instance()
                .getDefinitions().stream()
                .map(EntityDefinition::getId).forEach(crudEntity -> {
            registerPermissions(crudEntity);
            configureCrudSecurityConfigurer(crudEntity, CrudPermissions.staticPermissions(crudEntity));
        });

        AppPermissions.getAll().forEach(Permissions.instance()::registerStatic);

        Permissions.instance().scan(getClass().getPackage(), AuthorizationContexts.class.getPackage());
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
//
//    private void checkGeoCityImportStatus() throws Exception {
//        GeoCity geoCity = geoCityRepository.find(Query.build().filter(Filters.CAP, "75100")).findFirst().orElse(null);
//        if (geoCity == null) {
//
//            Resource res = resourceLoader.getResource("classpath:/csv/geoCities.csv");
//            UIFileUpload uiFileUpload = new UIFileUpload();
//
//            String path;
//            File file = null;
//            if (org.springframework.util.StringUtils.hasLength(optionsManager.get("file.getting.mode")) && optionsManager.get("file.getting.mode").equals("jar")){
//                file = new File(optionsManager.get("applica.framework.fileserver.basePath") + File.separator + "temp_geoCities.csv");
//                FileOutputStream out = new FileOutputStream(file);
//                IOUtils.copy(res.getInputStream(), out);
//                path = file.getAbsolutePath();
//            } else {
//                path = res.getFile().getAbsolutePath();
//            }
//
//            uiFileUpload.setPath(path);
//            csvFacade.importGeoCity(uiFileUpload);
//
//            File finalFile = file;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    if (finalFile != null && finalFile.exists()) {
//                        finalFile.delete();
//                    }
//                }
//            }).start();
//
//        }
//    }
//
//    private void checkGeoProvinceImportStatus() throws Exception {
//        GeoProvince geoProvince = geoProvinceRepository.find(null).findFirst().orElse(null);
//        if (geoProvince == null) {
//
//            Resource res = resourceLoader.getResource("classpath:/csv/geoProvinces.csv");
//            UIFileUpload uiFileUpload = new UIFileUpload();
//
//            String path;
//            File file = null;
//            if (org.springframework.util.StringUtils.hasLength(optionsManager.get("file.getting.mode")) && optionsManager.get("file.getting.mode").equals("jar")){
//                file = new File(optionsManager.get("applica.framework.fileserver.basePath")+ File.separator + "temp_geoProvinces.csv");
//                FileOutputStream out = new FileOutputStream(file);
//                IOUtils.copy(res.getInputStream(), out);
//                path = file.getAbsolutePath();
//            } else {
//                path = res.getFile().getAbsolutePath();
//            }
//
//            uiFileUpload.setPath(path);
//            csvFacade.importGeoProvince(uiFileUpload);
//
//            File finalFile = file;
//            new Thread(() -> {
//                if (finalFile != null && finalFile.exists()) {
//                    finalFile.delete();
//                }
//            }).start();
//
//        }
//    }


//
//    void createIndex() {
//        DB client = mongoHelper.getDB("default");
//        String query = "function createIndex(){   return db.getCollection('userLoginAttempts').createIndex( { mail: 1 },{background:true} );}";
//        MongoCommand mongoCommand = new MongoCommand(client, query);
//        mongoCommand.execute();
//
//        query = "function createIndex(){   return db.getCollection('dossiers').createIndex( { status: 1 },{background:true} );}";
//        mongoCommand = new MongoCommand(client, query);
//        mongoCommand.execute();
//
//        query = "function createIndex(){   return db.getCollection('revisions').createIndex( { entity: 1, entityId: 1},{background:true} );}";
//        mongoCommand = new MongoCommand(client, query);
//        mongoCommand.execute();
//
//    }

}
