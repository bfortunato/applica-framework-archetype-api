package applica.api.runner.operations;

import applica.api.domain.model.auth.User;
import applica.api.domain.model.users.AdminUser;
import applica.api.domain.utils.CustomUtils;
import applica.api.services.responses.ResponseCode;
import applica.framework.Entity;
import applica.framework.widgets.operations.OperationException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class AdminUserSaveOperation extends EntityCodedBaseSaveOperation {

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
        if (node.get("mail") == null || node.get("mail").isNull() || node.get("password") == null || node.get("password").isNull()){
            throw new OperationException(ResponseCode.ERROR_MAIL_AND_PASSWORD_REQUIRED);
        } else {
            User user = CustomUtils.createUser(node.get("mail").asText(), node.get("password").asText(), ((AdminUser) entity).getName(), ((AdminUser) entity).getLastname());
            ((AdminUser) entity).setUserId(user.getId());
        }
    }

}
