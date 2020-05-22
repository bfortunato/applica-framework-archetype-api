package applica.api.runner.operations;

import applica.api.domain.model.CodedEntity;
import applica.framework.Entity;
import applica.framework.widgets.operations.CreateOperation;
import applica.framework.widgets.operations.OperationException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EntityCodedBaseCreateOperation implements CreateOperation {

    @Override
    public ObjectNode create(Map<String, Object> params) throws OperationException {
        return null;
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return CodedEntity.class;
    }
}
