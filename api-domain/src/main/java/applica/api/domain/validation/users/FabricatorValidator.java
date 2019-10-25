package applica.api.domain.validation.users;

import applica.api.domain.model.users.Fabricator;
import applica.framework.Entity;
import applica.framework.library.validation.ValidationResult;
import applica.framework.library.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Component
public class FabricatorValidator implements Validator {

    @Autowired
    private PersonValidator personValidator;

    private static final String FISCAL_CODE_REGEX = "^[a-zA-Z]{6}[0-9]{2}[a-zA-Z][0-9]{2}[a-zA-Z][0-9]{3}[a-zA-Z]$";

    @Override
    public void validate(Entity entity, ValidationResult validationResult) {

        Fabricator fabricator = ((Fabricator) entity);

        personValidator.validate(fabricator, validationResult);

        if(!StringUtils.hasLength(fabricator.getBusinessName())) { validationResult.reject("businessName", "validation.user.businessName"); }

        if(!StringUtils.hasLength(fabricator.getAddress().getCountry())) { validationResult.reject("_country", "validation.user.country"); }
        if(!StringUtils.hasLength(fabricator.getAddress().getRegion())) { validationResult.reject("_region", "validation.user.region"); }
        if(!StringUtils.hasLength(fabricator.getAddress().getProvince())) { validationResult.reject("_province", "validation.user.province"); }
        if(!StringUtils.hasLength(fabricator.getAddress().getPostalCode())) { validationResult.reject("_postalCode", "validation.user.postalCode"); }
        if(!StringUtils.hasLength(fabricator.getAddress().getMunicipality())) { validationResult.reject("_municipality", "validation.user.municipality"); }
        if(!StringUtils.hasLength(fabricator.getAddress().getAddress())) { validationResult.reject("_address", "validation.user.address"); }
        if(!StringUtils.hasLength(fabricator.getAddress().getStreetNumber())) { validationResult.reject("_streetNumber", "validation.user.streetNumber"); }

        if(!StringUtils.hasLength(fabricator.getName())) { validationResult.reject("name", "validation.user.name"); }
        if(!StringUtils.hasLength(fabricator.getLastname())) { validationResult.reject("lastname", "validation.user.lastname"); }
        if(fabricator.getBirthDate() == null) { validationResult.reject("_birthDate", "validation.user.birthDate"); }
        if(!StringUtils.hasLength(fabricator.getBirthPlace())) { validationResult.reject("birthPlace", "validation.user.birthPlace"); }
        if(!StringUtils.hasLength(fabricator.getFiscalCode())) {
            validationResult.reject("fiscalCode", "validation.user.fiscalCode");
        } else {
            if (!Pattern.matches(FISCAL_CODE_REGEX, fabricator.getFiscalCode())){
                validationResult.reject("fiscalCode", "validation.user.invalid.fiscalCode");
            }
        }
    }

    @Override
    public Class getEntityType() {
        return Fabricator.class;
    }
}
