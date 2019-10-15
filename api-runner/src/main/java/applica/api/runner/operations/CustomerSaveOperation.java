package applica.api.runner.operations;

import applica.api.domain.model.geo.GeoCity;
import applica.api.domain.model.users.Customer;
import applica.framework.Entity;
import applica.framework.Repo;
import applica.framework.widgets.operations.BaseSaveOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class CustomerSaveOperation extends BaseSaveOperation {

    @Override
    public Class<? extends Entity> getEntityType() {
        return Customer.class;
    }

    @Override
    protected void finishEntity(ObjectNode node, Entity entity) {

        Customer customer = ((Customer) entity);

        if (node.get("_address") != null && !node.get("_address").isNull())
            customer.getAddress().setAddress(StringUtils.hasLength(node.get("_address").asText()) ? node.get("_address").asText() : null);
        if (node.get("_streetNumber") != null && !node.get("_streetNumber").isNull())
            customer.getAddress().setStreetNumber(StringUtils.hasLength(node.get("_streetNumber").asText()) ? node.get("_streetNumber").asText() : null);

        if (node.get("_city") != null && !node.get("_city").isNull()){

            if (node.get("_city").isObject()){
                customer.getAddress().setCity(Repo.of(GeoCity.class).get(node.get("_city").get("id").asText()).orElse(null));
            }
        }

        if (customer.getSubjectType() != null){
            if (customer.getSubjectType().equals(Customer.SUBJECT_TYPE_PHYSICAL_PERSON)) {
                if (node.get("_birthCity") != null && !node.get("_birthCity").isNull()){

                    if (node.get("_birthCity").isObject()){
                        customer.getAddress().setCity(Repo.of(GeoCity.class).get(node.get("_birthCity").get("id").asText()).orElse(null));
                    }
                }
            }
        }


    }

}
