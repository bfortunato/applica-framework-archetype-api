package applica.api.domain.validation.users;

import applica.api.domain.model.users.EndUser;
import applica.framework.Entity;
import applica.framework.library.validation.ValidationResult;
import applica.framework.library.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EndUserValidator implements Validator {

    @Autowired
    private PersonValidator personValidator;

    @Override
    public void validate(Entity entity, ValidationResult validationResult) {

        EndUser endUser = ((EndUser) entity);

        personValidator.validate(endUser, validationResult);

        if(!StringUtils.hasLength(endUser.getName())) { validationResult.reject("name", "validation.user.name"); }
        if(!StringUtils.hasLength(endUser.getLastname())) { validationResult.reject("lastname", "validation.user.lastname"); }
    }

    @Override
    public Class getEntityType() {
        return EndUser.class;
    }
}
