package applica.api.domain.validation.users;

import applica.api.domain.model.users.Fabricator;
import applica.framework.Entity;
import applica.framework.library.validation.ValidationResult;
import applica.framework.library.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class FabricatorValidator implements Validator {

    @Autowired
    private PersonValidator personValidator;

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
    }

    @Override
    public Class getEntityType() {
        return Fabricator.class;
    }
}
