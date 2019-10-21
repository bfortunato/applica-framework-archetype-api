package applica.api.domain.validation.users;

import applica.api.domain.model.dossiers.DocumentType;
import applica.framework.Entity;
import applica.framework.library.validation.ValidationResult;
import applica.framework.library.validation.Validator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
public class DocumentTypeValidator implements Validator {

    @Override
    public void validate(Entity entity, ValidationResult validationResult) {
        DocumentType documentType = ((DocumentType) entity);
        if(!StringUtils.hasLength(documentType.getDescription())) { validationResult.reject("description", "validation.documentType.description"); }
        if(!StringUtils.hasLength(documentType.getAssignationType())) { validationResult.reject("assignationType", "validation.documentType.assignationType"); }
        if(!StringUtils.hasLength(documentType.getTypology())) {
            validationResult.reject("typology", "validation.documentType.typology");
        } else if (!Objects.equals(documentType.getTypology(), DocumentType.TYPOLOGY_NO_MODEL)){
            if(documentType.getTemplate() == null) { validationResult.reject("_template", "validation.documentType.template"); }
        }
    }

    @Override
    public Class getEntityType() {
        return DocumentType.class;
    }
}
