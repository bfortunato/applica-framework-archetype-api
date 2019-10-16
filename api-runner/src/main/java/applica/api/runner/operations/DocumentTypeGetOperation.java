package applica.api.runner.operations;

import applica.api.domain.model.dossiers.DocumentType;
import applica.api.runner.facade.AttachmentFacade;
import applica.framework.Entity;
import applica.framework.widgets.operations.BaseGetOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentTypeGetOperation extends BaseGetOperation {

    @Autowired
    private AttachmentFacade attachmentFacade;

    @Override
    protected void finishNode(Entity entity, ObjectNode node) {
        attachmentFacade.attachmentToDataUrl(entity, node, "template", "_template");
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return DocumentType.class;
    }


}
