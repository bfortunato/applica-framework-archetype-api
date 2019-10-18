package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.domain.model.auth.User;
import applica.api.domain.model.users.Customer;
import applica.api.services.CustomersService;
import applica.framework.Filter;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomersService {

    @Override
    public List<Customer> findCustomerByKeyword(String keyword, boolean includeUser) {
        var query = Query.build();
        if (StringUtils.isNotEmpty(keyword)) {
            query.disjunction()
                    .like(Filters.SOCIAL_REASON, keyword)
                    .like(Filters.FIRST_NAME, keyword)
                    .like(Filters.LAST_NAME, keyword)
                    .finish();
        }
        List<Customer> customers = Repo.of(Customer.class).find(query).getRows();
        if (includeUser) {
            for (Customer c: customers
                 ) {
                materializeUser(c);
            }
        }
        return customers;
    }

    @Override
    public Result<Customer> findCustomerByQuery(Query query) {
        return Repo.of(Customer.class).find(query);
    }

    @Override
    public Result<Customer> findActiveCustomerByQuery(Query query) {
        if (query == null)
            query = Query.build();
        if (StringUtils.isNotEmpty(query.getKeyword())) {
            query.builder().disjunction()
                    .like(Filters.SOCIAL_REASON, query.getKeyword())
                    .like(Filters.FIRST_NAME, query.getKeyword())
                    .like(Filters.LAST_NAME, query.getKeyword())
                    .finish();
        }
        query.getFilters().add(new Filter(Filters.ACTIVE, true, Filter.EQ));
        return Repo.of(Customer.class).find(query);
    }

    @Override
    public void saveCustomer(Customer customer) {
        Repo.of(Customer.class).save(customer);
    }

    @Override
    public void deleteCustomer(Object customerId) {
        //TODO: Potrò cancellare il cliente solo se non ha pratiche
//        Repo.of(Customer.class).delete(customerId);
    }

    @Override
    public void materializeUser(Customer customer) {
        customer.setUser(Repo.of(User.class).get(customer.getUserId()).orElse(null));
    }
}
