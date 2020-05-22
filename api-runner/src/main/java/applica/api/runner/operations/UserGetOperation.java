package applica.api.runner.operations;

import applica.framework.Entity;
import applica.framework.widgets.mapping.EntityMapper;
import applica.framework.widgets.operations.BaseGetOperation;
import applica.framework.widgets.operations.OperationException;
import applica.api.domain.model.auth.User;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

@Component
public class UserGetOperation extends BaseGetOperation {

    private final EntityMapper entityMapper;

    public UserGetOperation(EntityMapper entityMapper) {
        this.entityMapper = entityMapper;
    }

    @Override
    protected void finishNode(Entity entity, ObjectNode node) throws OperationException {
        entityMapper.imageToDataUrl(entity, node, "image", "_image", "250x250");
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return User.class;
    }
}
