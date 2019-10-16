package applica.api.runner.operations;

import applica.api.domain.model.dossiers.DocumentType;
import applica.api.runner.facade.AttachmentFacade;
import applica.framework.Entity;
import applica.framework.widgets.operations.OperationException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentTypeSaveOperation extends EntityCodedBaseSaveOperation {

    @Autowired
    private AttachmentFacade attachmentFacade;

    @Override
    public Class<? extends Entity> getEntityType() {
        return DocumentType.class;
    }

    @Override
    protected void finishEntity(ObjectNode node, Entity entity) throws OperationException {

        attachmentFacade.dataUrlToAttachment(node, entity, "_template", "template", "files/documentTypes");

    }
}
