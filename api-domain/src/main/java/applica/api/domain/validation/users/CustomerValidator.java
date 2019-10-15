package applica.api.domain.validation.users;

import applica.api.domain.model.users.Customer;
import applica.framework.Entity;
import applica.framework.library.validation.ValidationResult;
import applica.framework.library.validation.Validator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CustomerValidator implements Validator {

    @Override
    public void validate(Entity entity, ValidationResult validationResult) {

        Customer customer = ((Customer) entity);

        if (customer.getSubjectType().equals(Customer.SUBJECT_TYPE_PHYSICAL_PERSON)) {
            if (!StringUtils.hasLength(customer.getFirstName())) {
                validationResult.reject("firstName", "validation.user.name");
            }
            if (!StringUtils.hasLength(customer.getLastName())) {
                validationResult.reject("lastName", "validation.user.lastname");
            }
        }
    }

    @Override
    public Class getEntityType() {
        return Customer.class;
    }
}
