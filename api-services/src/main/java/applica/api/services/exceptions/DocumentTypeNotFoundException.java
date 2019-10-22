package applica.api.services.exceptions;

public class DocumentTypeNotFoundException extends Exception {

    private final Object documentTypeId;

    public DocumentTypeNotFoundException(Object documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public Object getDocumentTypeId() {
        return documentTypeId;
    }
}
