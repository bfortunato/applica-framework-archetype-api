package applica.api.runner.operations;

import applica.api.domain.model.auth.User;
import applica.api.domain.model.users.Fabricator;
import applica.api.runner.facade.AccountFacade;
import applica.api.services.DocumentsService;
import applica.api.services.UserService;
import applica.api.services.exceptions.UserAlreadyExistException;
import applica.api.services.responses.ResponseCode;
import applica.framework.Entity;
import applica.framework.Repo;
import applica.framework.library.utils.DateUtils;
import applica.framework.security.authorization.AuthorizationException;
import applica.framework.widgets.operations.OperationException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

@Component
public class FabricatorSaveOperation extends EntityCodedBaseSaveOperation {

    @Autowired
    private DocumentsService documentsService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountFacade accountFacade;

    @Override
    public Class<? extends Entity> getEntityType() {
        return Fabricator.class;
    }

    @Override
    protected void finishEntity(ObjectNode node, Entity entity) {

        Fabricator fabricator = ((Fabricator) entity);

//        if (node.get("_address") != null && !node.get("_address").isNull())
//            fabricator.getAddress().setAddress(StringUtils.hasLength(node.get("_address").asText()) ? node.get("_address").asText() : null);
//        if (node.get("_streetNumber") != null && !node.get("_streetNumber").isNull())
//            fabricator.getAddress().setStreetNumber(StringUtils.hasLength(node.get("_streetNumber").asText()) ? node.get("_streetNumber").asText() : null);
//
//        if (node.get("_city") != null && !node.get("_city").isNull()){
//
//            if (node.get("_city").isObject()){
//                fabricator.getAddress().setCity(Repo.of(GeoCity.class).get(node.get("_city").get("id").asText()).orElse(null));
//            }
//        }

        fabricator.getAddress().setCountry(node.get("_country") != null ? node.get("_country").asText() : null);
        fabricator.getAddress().setRegion(node.get("_region") != null ? node.get("_region").asText() : null);
        fabricator.getAddress().setProvince(node.get("_province") != null ? node.get("_province").asText() : null);
        fabricator.getAddress().setMunicipality(node.get("_municipality") != null ? node.get("_municipality").asText() : null);
        fabricator.getAddress().setPostalCode(node.get("_postalCode") != null ?node.get("_postalCode").asText() : null);
        fabricator.getAddress().setAddress(node.get("_address") != null ? node.get("_address").asText() : null);
        fabricator.getAddress().setStreetNumber(node.get("_streetNumber") != null ? node.get("_streetNumber").asText() : null);

        if (node.get("_category") != null){
            fabricator.setCategoryId(node.get("_category").get("id").asText());
        }

        if (node.get("_birthDate") != null && !node.get("_birthDate").isNull()){
            try {
                fabricator.setBirthDate(DateUtils.getDateFromString(node.get("_birthDate").asText(), DateUtils.FORMAT_DATE_DATEPICKER));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void beforeSave(ObjectNode node, Entity entity) throws OperationException {
        super.beforeSave(node, entity);
        Fabricator fabricator = ((Fabricator) entity);

        if (Objects.isNull(fabricator.getId())){
            ((Fabricator) entity).setDocuments(documentsService.generateFabricatorDocuments());
        }

        User user = null;
        //Se non ho lo user Id significa che non ho creato l'utenza di riferimento
        if (Objects.nonNull(fabricator.getUserId())){
            user = Repo.of(User.class).get(((Fabricator) entity).getUserId()).orElse(null);
        }

        //se non ho l'utenza di riferimento
        if (user == null) {
            //e ho abilitato l'accesso all'app, creo l'utente e lo associo al serramentista
            if (fabricator.isAppEnabled()) {
                if (node.get("mail") == null || node.get("mail").isNull())
                    throw new OperationException(ResponseCode.ERROR_MAIL_REQUIRED);

                try {
                    user = userService.createUser(node.get("mail").asText(), fabricator.getName(), fabricator.getLastname());
                    ((Fabricator) entity).setUserId(user.getId());
                } catch (UserAlreadyExistException e) {
                    throw new OperationException(ResponseCode.ERROR_MAIL_ALREADY_EXISTS);
                }

            }
        } else {
            //altrimenti aggiorno i dati se necessario
            try {
                userService.updateUserIfNecessary(user, node);
            } catch (UserAlreadyExistException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void afterSave(ObjectNode node, Entity entity) {
        Fabricator fabricator = ((Fabricator) entity);
        User user = Repo.of(User.class).get(fabricator.getUserId()).orElse(null);
        if (user != null && user.getFirstLogin() == null) {
            user.setRegistrationDate(new Date());
            user.setFirstLogin(true);
            try {
                accountFacade.generateAndSendUserOneTimePassword(user);
            } catch (AuthorizationException e) {
                e.printStackTrace();
            }
        }
    }

}
