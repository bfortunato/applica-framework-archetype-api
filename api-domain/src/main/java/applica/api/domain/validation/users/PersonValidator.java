package applica.api.domain.validation.users;

import applica.api.domain.model.users.Person;
import applica.framework.Entity;
import applica.framework.library.validation.ValidationResult;
import applica.framework.library.validation.Validator;
import org.springframework.stereotype.Component;

@Component
public class PersonValidator implements Validator {

    @Override
    public void validate(Entity entity, ValidationResult validationResult) {
        Person person = ((Person) entity);
        if(person.getCategoryId() == null) { validationResult.reject("_category", "validation.user.category"); }
    }

    @Override
    public Class getEntityType() {
        return Person.class;
    }
}
