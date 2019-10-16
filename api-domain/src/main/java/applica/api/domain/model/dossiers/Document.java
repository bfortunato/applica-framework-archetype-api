package applica.api.domain.model.dossiers;

import applica.framework.AEntity;

public class Document extends AEntity {

    private Object documentTypeId;
    private String file;
    private boolean valid;

    public Document(Object documentTypeId, String file) {
        this.documentTypeId = documentTypeId;
        this.file = file;
        this.valid = true;
    }

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

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
