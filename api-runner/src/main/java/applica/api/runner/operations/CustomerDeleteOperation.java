package applica.api.runner.operations;

import applica.api.domain.model.users.Customer;
import applica.api.services.CustomersService;
import applica.framework.Entity;
import applica.framework.widgets.operations.BaseDeleteOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class CustomerDeleteOperation extends BaseDeleteOperation {

    @Autowired
    private CustomersService customersService;

    @Override
    public void delete(List<String> ids) {
        ids.forEach(id -> {
            customersService.deleteCustomer(id);
        });
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return Customer.class;
    }


}
