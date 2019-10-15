package applica.api.runner.operations;

import applica.api.domain.model.users.Customer;
import applica.framework.Entity;
import applica.framework.widgets.operations.BaseSaveOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

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

//        if (node.get("_address") != null && !node.get("_address").isNull())
//            customer.getAddress().setAddress(StringUtils.hasLength(node.get("_address").asText()) ? node.get("_address").asText() : null);
//        if (node.get("_streetNumber") != null && !node.get("_streetNumber").isNull())
//            customer.getAddress().setStreetNumber(StringUtils.hasLength(node.get("_streetNumber").asText()) ? node.get("_streetNumber").asText() : null);
//
//        if (node.get("_city") != null && !node.get("_city").isNull()){
//
//            if (node.get("_city").isObject()){
//                customer.getAddress().setCity(Repo.of(GeoCity.class).get(node.get("_city").get("id").asText()).orElse(null));
//            }
//        }

        customer.getAddress().setAddress(node.get("_address") != null ? node.get("_address").asText() : null);
        customer.getAddress().setCountry(node.get("_country") != null ? node.get("_country").asText() : null);
        customer.getAddress().setMunicipality(node.get("_municipality") != null ? node.get("_municipality").asText() : null);
        customer.getAddress().setMunicipality(node.get("_streetNumber") != null ? node.get("_streetNumber").asText() : null);
        customer.getAddress().setMunicipality(node.get("_postalCode") != null ?node.get("_postalCode").asText() : null);

        if (customer.getSubjectType() != null){
            if (customer.getSubjectType().equals(Customer.SUBJECT_TYPE_PHYSICAL_PERSON)) {
//                if (node.get("_birthCity") != null && !node.get("_birthCity").isNull()){
//
//                    if (node.get("_birthCity").isObject()){
//                        customer.getBirthAddress().setCity(Repo.of(GeoCity.class).get(node.get("_birthCity").get("id").asText()).orElse(null));
//                    }
//                }
            }
        }


    }

}
