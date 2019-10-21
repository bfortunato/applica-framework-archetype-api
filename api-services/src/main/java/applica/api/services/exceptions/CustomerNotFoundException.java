package applica.api.services.exceptions;

public class CustomerNotFoundException extends Exception {

    private final Object customerId;

    public CustomerNotFoundException(Object customerId) {
        this.customerId = customerId;
    }

    public Object getCustomerId() {
        return customerId;
    }
}
