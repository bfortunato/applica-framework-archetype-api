package applica.api.services.exceptions;

public class DossierNotFoundException extends Exception {

    private final Object dossierId;

    public DossierNotFoundException(Object dossierId) {
        this.dossierId = dossierId;
    }

    public Object getDossierId() {
        return dossierId;
    }
}
