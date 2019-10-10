package applica.api.domain.model.dossiers;

import applica.framework.AEntity;

public class Document extends AEntity {

    private Object documentTypeId;
    private String file;

    public Object getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Object documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
