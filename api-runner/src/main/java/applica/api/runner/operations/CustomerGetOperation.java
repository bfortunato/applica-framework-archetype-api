package applica.api.runner.operations;

import applica.api.domain.model.users.Customer;
import applica.api.domain.model.users.categories.FabricatorCategory;
import applica.framework.Entity;
import applica.framework.Repo;
import applica.framework.widgets.operations.BaseGetOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class CustomerGetOperation extends BaseGetOperation {

    @Override
    protected void finishNode(Entity entity, ObjectNode node) {

        Customer customer = ((Customer) entity);
        node.putPOJO("_category", Repo.of(FabricatorCategory.class).get(customer.getCategoryId()).orElse(null));

        node.put("_country", customer.getAddress().getCountry());
        node.put("_region", customer.getAddress().getRegion());
        node.put("_province", customer.getAddress().getProvince());
        node.put("_municipality", customer.getAddress().getMunicipality());
        node.put("_postalCode", customer.getAddress().getPostalCode());
        node.put("_address", customer.getAddress().getAddress());
        node.put("_streetNumber", customer.getAddress().getStreetNumber());
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return Customer.class;
    }


}
