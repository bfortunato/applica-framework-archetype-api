package applica.api.domain.model.dossiers;

import applica.framework.AEntity;

import java.util.Date;

public class Document extends AEntity {

    private Object documentTypeId;
    private String file;
    private boolean valid;
    private Date uploadDate;
    private Date refusedDate;

    private transient DocumentType documentType;

    public Document() {}

    public Document(Object documentTypeId) {
        this.documentTypeId = documentTypeId;
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

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Date getRefusedDate() {
        return refusedDate;
    }

    public void setRefusedDate(Date refusedDate) {
        this.refusedDate = refusedDate;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
}
