package applica.api.runner.operations;

import applica.api.domain.model.auth.User;
import applica.api.domain.model.users.AdminUser;
import applica.api.runner.facade.AccountFacade;
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

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class AdminUserSaveOperation extends EntityCodedBaseSaveOperation {

    @Autowired
    private AccountFacade accountFacade;

    @Autowired
    private UserService userService;

    @Override
    public Class<? extends Entity> getEntityType() {
        return AdminUser.class;
    }

    @Override
    protected void finishEntity(ObjectNode node, Entity entity) {

        AdminUser adminUser = ((AdminUser) entity);

        if (node.get("_category") != null){
            adminUser.setCategoryId(node.get("_category").get("id").asText());
        }
    }

    @Override
    protected void beforeSave(ObjectNode node, Entity entity) throws OperationException {
        super.beforeSave(node, entity);
        if (entity.getId() == null && node.get("mail") == null || node.get("mail").isNull()){
            throw new OperationException(ResponseCode.ERROR_MAIL_REQUIRED);
        } else if (entity.getId() == null){
            User user = null;
            try {
                user = userService.createUser(node.get("mail").asText(), ((AdminUser) entity).getName(), ((AdminUser) entity).getLastname());
                ((AdminUser) entity).setUserId(user.getId());
            } catch (UserAlreadyExistException e) {
                throw new OperationException(ResponseCode.ERROR_MAIL_ALREADY_EXISTS);
            }
        } else {
            User user = Repo.of(User.class).get(((AdminUser) entity).getUserId()).orElse(null);
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
        AdminUser adminUser = ((AdminUser) entity);
        User user = Repo.of(User.class).get(adminUser.getUserId()).orElse(null);
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
