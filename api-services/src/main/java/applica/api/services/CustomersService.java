package applica.api.services;

import applica.api.domain.model.users.Customer;
import applica.api.domain.model.users.Person;

import java.util.List;

public interface CustomersService {

    List<Customer> findCustomerByKeyword(String keyword);
    void saveCustomer(Customer customer);
    void deleteCustomer(Object customerId);

}
