package applica.api.services;

import applica.api.domain.model.users.Customer;
import applica.framework.Query;
import applica.framework.Result;

import java.util.List;

public interface CustomersService {

    List<Customer> findCustomerByKeyword(String keyword);
    Result<Customer> findCustomerByQuery(Query query);
    void saveCustomer(Customer customer);
    void deleteCustomer(Object customerId);

}
