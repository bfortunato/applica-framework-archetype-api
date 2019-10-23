package applica.api.runner.operations;

import applica.api.domain.model.auth.User;
import applica.api.domain.model.users.EndUser;
import applica.api.domain.model.users.categories.EndUserCategory;
import applica.framework.Entity;
import applica.framework.Repo;
import applica.framework.widgets.operations.BaseGetOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class EndUserGetOperation extends BaseGetOperation {

    @Override
    protected void finishNode(Entity entity, ObjectNode node) {
        map().imageToDataUrl(entity, node, "avatar", "_avatar", "150x*");
        node.putPOJO("_category", Repo.of(EndUserCategory.class).get(((EndUser) entity).getCategoryId()).orElse(null));

        User user = Repo.of(User.class).get(((EndUser) entity).getUserId()).orElse(null);
        if (user != null){
            node.put("mail", user.getMail());
        }
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return EndUser.class;
    }


}
