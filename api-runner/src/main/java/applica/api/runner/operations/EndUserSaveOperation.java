package applica.api.runner.operations;

import applica.api.domain.model.auth.User;
import applica.api.domain.model.users.EndUser;
import applica.api.services.UserService;
import applica.api.services.exceptions.UserAlreadyExistException;
import applica.api.services.responses.ResponseCode;
import applica.framework.Entity;
import applica.framework.Repo;
import applica.framework.widgets.operations.OperationException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class EndUserSaveOperation extends EntityCodedBaseSaveOperation {

    @Autowired
    private UserService userService;

    @Override
    public Class<? extends Entity> getEntityType() {
        return EndUser.class;
    }

    @Override
    protected void finishEntity(ObjectNode node, Entity entity) {

        EndUser endUser = ((EndUser) entity);

        map().dataUrlToImage(node, entity, "_avatar", "avatar", "images/users");
        if (node.get("_category") != null){
            endUser.setCategoryId(node.get("_category").get("id").asText());
        }
    }

    @Override
    protected void beforeSave(ObjectNode node, Entity entity) throws OperationException {
        super.beforeSave(node, entity);
        if (entity.getId() == null && node.get("mail") == null || node.get("mail").isNull() || node.get("password") == null || node.get("password").isNull()){
            throw new OperationException(ResponseCode.ERROR_MAIL_AND_PASSWORD_REQUIRED);
        } else if (entity.getId() == null){
            User user = null;
            try {
                user = userService.createUser(node.get("mail").asText(), node.get("password").asText(), ((EndUser) entity).getName(), ((EndUser) entity).getLastname());
                ((EndUser) entity).setUserId(user.getId());
            } catch (UserAlreadyExistException e) {
                throw new OperationException(ResponseCode.ERROR_MAIL_ALREADY_EXISTS);
            }
        } else {
            User user = Repo.of(User.class).get(((EndUser) entity).getUserId()).orElse(null);
            if (user != null) {
                try {
                    userService.updateUserIfNecessary(user, node);
                } catch (UserAlreadyExistException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
