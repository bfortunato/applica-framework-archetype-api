package applica.api.services.exceptions;

public class DocumentNotFoundException extends Exception {

    private final Object documentId;

    public DocumentNotFoundException(Object documentId) {
        this.documentId = documentId;
    }

    public Object getDocumentId() {
        return documentId;
    }
}
