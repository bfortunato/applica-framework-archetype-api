package applica.api.services;

import applica.api.domain.model.users.Customer;
import applica.api.services.exceptions.UserAlreadyExistException;
import applica.framework.Query;
import applica.framework.Result;

import java.util.List;

public interface CustomersService {

    List<Customer> findCustomerByKeyword(String keyword, boolean includeUser);
    Result<Customer> findCustomerByQuery(Query query);
    Result<Customer> findActiveCustomerByQuery(Query query);
    void saveCustomer(Customer customer, String mail, String password) throws UserAlreadyExistException;
    void deleteCustomer(Object customerId);
    void materializeUser(Customer customer);



}
