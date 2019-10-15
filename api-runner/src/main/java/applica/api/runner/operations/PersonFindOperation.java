package applica.api.runner.operations;

import applica.api.domain.model.users.Person;
import applica.framework.Entity;
import applica.framework.widgets.operations.BaseFindOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

@Component
public class PersonFindOperation extends BaseFindOperation {

    @Override
    public Class<? extends Entity> getEntityType() {
        return Person.class;
    }

    @Override
    public void onSerializeEntity(ObjectNode node, Entity entity) {
        Person person = ((Person) entity);
    }
}
