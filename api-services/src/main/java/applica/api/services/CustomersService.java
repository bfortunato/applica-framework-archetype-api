package applica.api.services;

import applica.api.domain.model.users.Person;

import java.util.List;

public interface CustomersService {

    List<Person> findCustomerByKeyword(String keyword);
    void saveCustomer(Person customer);
    void deleteCustomer(Object customerId);

}
