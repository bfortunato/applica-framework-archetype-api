package applica.api.services.exceptions;

public class FabricatorNotFoundException extends Exception {

    private final Object fabricatorId;

    public FabricatorNotFoundException(Object fabricatorId) {
        this.fabricatorId = fabricatorId;
    }

    public Object getFabricatorId() {
        return fabricatorId;
    }
}
