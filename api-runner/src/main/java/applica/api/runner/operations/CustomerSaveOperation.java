package applica.api.runner.operations;

import applica.api.domain.model.auth.User;
import applica.api.domain.model.users.Customer;
import applica.api.services.UserService;
import applica.api.services.exceptions.UserAlreadyExistException;
import applica.api.services.responses.ResponseCode;
import applica.framework.Entity;
import applica.framework.Repo;
import applica.framework.widgets.operations.OperationException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class CustomerSaveOperation extends EntityCodedBaseSaveOperation {

    @Autowired
    private UserService userService;

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

        if (customer.getSubjectType() != null){
            if (customer.getSubjectType().equals(Customer.SUBJECT_TYPE_PHYSICAL_PERSON)) {
//                if (node.get("_birthCity") != null && !node.get("_birthCity").isNull()){
//
//                    if (node.get("_birthCity").isObject()){
//                        customer.getBirthAddress().setCity(Repo.of(GeoCity.class).get(node.get("_birthCity").get("id").asText()).orElse(null));
//                    }
//                }
                customer.getBirthAddress().setCountry(node.get("_birthCountry") != null ? node.get("_birthCountry").asText() : null);
                customer.getBirthAddress().setRegion(node.get("_birthRegion") != null ? node.get("_birthRegion").asText() : null);
                customer.getBirthAddress().setProvince(node.get("_birthProvince") != null ? node.get("_birthProvince").asText() : null);
                customer.getBirthAddress().setMunicipality(node.get("_birthMunicipality") != null ? node.get("_birthMunicipality").asText() : null);
                customer.getBirthAddress().setPostalCode(node.get("_birthPostalCode") != null ?node.get("_birthPostalCode").asText() : null);
            }
        }

        customer.getAddress().setCountry(node.get("_country") != null && !node.get("_country").isNull() ? node.get("_country").asText() : null);
        customer.getAddress().setRegion(node.get("_region") != null && !node.get("_region").isNull() ? node.get("_region").asText() : null);
        customer.getAddress().setProvince(node.get("_province") != null && !node.get("_province").isNull() ? node.get("_province").asText() : null);
        customer.getAddress().setMunicipality(node.get("_municipality") != null && !node.get("_municipality").isNull() ? node.get("_municipality").asText() : null);
        customer.getAddress().setPostalCode(node.get("_postalCode") != null && !node.get("_postalCode").isNull() ? node.get("_postalCode").asText() : null);
        customer.getAddress().setAddress(node.get("_address") != null && !node.get("_address").isNull() ? node.get("_address").asText() : null);
        customer.getAddress().setStreetNumber(node.get("_streetNumber") != null && !node.get("_streetNumber").isNull() ? node.get("_streetNumber").asText() : null);

    }

    @Override
    protected void beforeSave(ObjectNode node, Entity entity) throws OperationException {
        super.beforeSave(node, entity);
        if (entity.getId() == null && node.get("mail") == null || node.get("mail").isNull()){
            throw new OperationException(ResponseCode.ERROR_MAIL_AND_PASSWORD_REQUIRED);
        } else if (entity.getId() == null){
            User user = null;
            Customer customer = ((Customer) entity);
            try {
                user = userService.createUser(node.get("mail").asText(), Objects.equals(customer.getSubjectType(), Customer.SUBJECT_TYPE_PHYSICAL_PERSON) ? customer.getFirstName() : customer.getSocialReason(), customer.getLastName());
                ((Customer) entity).setUserId(user.getId());
            } catch (UserAlreadyExistException e) {
                throw new OperationException(ResponseCode.ERROR_MAIL_ALREADY_EXISTS);
            }
        } else {
            User user = Repo.of(User.class).get(((Customer) entity).getUserId()).orElse(null);
            if (user != null) {
                try {
                    userService.updateUserIfNecessary(user, node);
                } catch (UserAlreadyExistException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
