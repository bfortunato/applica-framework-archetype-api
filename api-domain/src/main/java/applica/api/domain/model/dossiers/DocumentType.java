package applica.api.domain.model.dossiers;

import applica.api.domain.model.CodedEntity;
import applica.api.domain.model.EntityList;
import applica.framework.widgets.entities.EntityId;
import applica.framework.widgets.mapping.Attachment;

@EntityId(EntityList.DOCUMENT_TYPE)
public class DocumentType extends CodedEntity {

    private String description;
    private boolean required;
    private String information;
    private Attachment template;
    private boolean active;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public Attachment getTemplate() {
        return template;
    }

    public void setTemplate(Attachment template) {
        this.template = template;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
