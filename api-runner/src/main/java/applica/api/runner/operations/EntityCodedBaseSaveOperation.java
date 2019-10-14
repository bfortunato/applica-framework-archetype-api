package applica.api.runner.operations;

import applica.api.domain.model.CodedEntity;
import applica.api.services.CodeGeneratorService;
import applica.framework.Entity;
import applica.framework.widgets.operations.BaseSaveOperation;
import applica.framework.widgets.operations.OperationException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public abstract class EntityCodedBaseSaveOperation extends BaseSaveOperation {


    @Autowired
    private CodeGeneratorService codeGeneratorService;

    @Override
    protected void beforeSave(ObjectNode node, Entity entity) throws OperationException {
        if(entity.getId() == null)
        {
            ((CodedEntity) entity).setCode(codeGeneratorService.getFirstAvailableCode(getEntityType()));
        }
    }
}
