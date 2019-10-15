package applica.api.domain.validation.users;

import applica.api.domain.model.auth.User;
import applica.framework.Entity;
import applica.framework.library.validation.ValidationResult;
import applica.framework.library.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Applica (www.applica.guru)
 * User: bimbobruno
 * Date: 05/11/13
 * Time: 18:26
 */
@Component
public class UserValidator implements Validator {

    @Autowired
    private PersonValidator personValidator;

    @Override
    public void validate(Entity entity, ValidationResult validationResult) {

        User user = ((User) entity);

        personValidator.validate(user, validationResult);

        if(!StringUtils.hasLength(user.getName())) { validationResult.reject("name", "validation.user.name"); }
//        if(!StringUtils.hasLength(user.getLastname())) { validationResult.reject("lastname", "validation.user.lastname"); }

    }

    @Override
    public Class getEntityType() {
        return User.class;
    }

}
