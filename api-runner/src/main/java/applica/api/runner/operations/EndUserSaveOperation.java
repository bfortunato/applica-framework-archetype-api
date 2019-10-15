package applica.api.runner.operations;

import applica.api.domain.model.users.EndUser;
import applica.api.runner.facade.AccountFacade;
import applica.framework.Entity;
import applica.framework.widgets.operations.BaseSaveOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class EndUserSaveOperation extends BaseSaveOperation {

    @Autowired
    private AccountFacade accountFacade;

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

}
