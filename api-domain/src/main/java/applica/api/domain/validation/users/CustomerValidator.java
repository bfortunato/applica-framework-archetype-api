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
            if (!StringUtils.hasLength(customer.getSex())) {
                validationResult.reject("sex", "validation.user.sex");
            }
            if (customer.getBirthAddress().getMunicipality() == null) {
                validationResult.reject("_birthCity", "validation.user.city");
            }
            if (!StringUtils.hasLength(customer.getFiscalCode())) {
                validationResult.reject("fiscalCode", "validation.user.fiscalCode");
            }

        } else {
            if (!StringUtils.hasLength(customer.getSocialReason())) {
                validationResult.reject("socialReason", "validation.user.socialReason");
            }
            if (customer.isVatCodeDisabled()){
                if (!StringUtils.hasLength(customer.getFiscalCode())) {
                    validationResult.reject("fiscalCode", "validation.user.fiscalCode");
                }
            } else {
                if (!StringUtils.hasLength(customer.getVatCode())) {
                    validationResult.reject("vatCode", "validation.user.vatCode");
                }
            }
        }

        if (!StringUtils.hasLength(customer.getPhoneNumber())) {
            validationResult.reject("phoneNumber", "validation.user.phoneNumber");
        }
    }

    @Override
    public Class getEntityType() {
        return Customer.class;
    }
}
