package applica.api.domain.model.dossiers;

import applica.framework.AEntity;

import java.util.Date;

public class Document extends AEntity {

    public static final String TO_BE_UPLOAD = "to-be-upload";
    public static final String UPLOADED = "uploaded";
    public static final String TO_RECHARGE = "to-recharge";

    private Object documentTypeId;
    private String file;
    private boolean valid;
    private Date uploadDate;
    private Date refusedDate;
    private String refuseReason;
    private String preview;
    private String status;

    private transient DocumentType documentType;

    public Document() {}

    public Document(Object documentTypeId) {
        this.documentTypeId = documentTypeId;
        this.status = TO_BE_UPLOAD;
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

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
