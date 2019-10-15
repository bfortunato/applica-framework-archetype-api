package applica.api.runner.operations;

import applica.api.domain.model.users.Customer;
import applica.framework.Entity;
import applica.framework.widgets.operations.BaseFindOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

@Component
public class CustomerFindOperation extends BaseFindOperation {

    @Override
    public Class<? extends Entity> getEntityType() {
        return Customer.class;
    }

    @Override
    public void onSerializeEntity(ObjectNode node, Entity entity) {
        Customer customer = ((Customer) entity);
    }
}
