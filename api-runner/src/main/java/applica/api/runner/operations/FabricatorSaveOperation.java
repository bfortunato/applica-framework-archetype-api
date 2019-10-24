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
import applica.framework.security.authorization.AuthorizationException;
import applica.framework.widgets.operations.OperationException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

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
    }

    @Override
    protected void beforeSave(ObjectNode node, Entity entity) throws OperationException {
        super.beforeSave(node, entity);
        if (entity.getId() == null && (node.get("mail") == null || node.get("mail").isNull())){
            throw new OperationException(ResponseCode.ERROR_MAIL_AND_PASSWORD_REQUIRED);
        } else if (entity.getId() == null){
            User user = null;
            try {
                user = userService.createUser(node.get("mail").asText(), ((Fabricator) entity).getBusinessName(), null);
                ((Fabricator) entity).setUserId(user.getId());
            } catch (UserAlreadyExistException e) {
                throw new OperationException(ResponseCode.ERROR_MAIL_ALREADY_EXISTS);
            }
            ((Fabricator) entity).setDocuments(documentsService.generateFabricatorDocuments());
        } else {
            User user = Repo.of(User.class).get(((Fabricator) entity).getUserId()).orElse(null);
            if (user != null) {
                try {
                    userService.updateUserIfNecessary(user, node);
                } catch (UserAlreadyExistException e) {
                    e.printStackTrace();
                }
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
