package applica.api.domain.model.dossiers;

import applica.api.domain.model.CodedEntity;
import applica.api.domain.model.EntityList;
import applica.framework.widgets.entities.EntityId;
import applica.framework.widgets.mapping.Attachment;

@EntityId(EntityList.DOCUMENT_TYPE)
public class DocumentType extends CodedEntity {

    public static final String FABRICATOR_PROFILE = "fabricator-profile";
    public static final String PREPARATORY_DOCUMENTATION = "preparatory-documentation";
    public static final String PRIVATE_CLOSING_DOCUMENTATION = "private-closing-documentation";
    public static final String PUBLIC_CLOSING_DOCUMENTATION = "public-closing-documentation";

    public static final String TYPOLOGY_NO_MODEL = "no-model";
    public static final String TYPOLOGY_DOWNLOADABLE_TEMPLATE = "downloadable-template";
    public static final String TYPOLOGY_SELF_COMPILED_DOWNLOADABLE_TEMPLATE = "self-compiled-downloadable-template";

    private String description;
    private String information;
    private Attachment template;
    private boolean active;
    private String assignationType;
    private String typology;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getAssignationType() {
        return assignationType;
    }

    public void setAssignationType(String assignationType) {
        this.assignationType = assignationType;
    }

    public String getTypology() {
        return typology;
    }

    public void setTypology(String typology) {
        this.typology = typology;
    }
}
