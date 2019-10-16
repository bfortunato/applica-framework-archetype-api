package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.domain.model.users.Customer;
import applica.api.services.CustomersService;
import applica.framework.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomersService {

    @Override
    public List<Customer> findCustomerByKeyword(String keyword) {
        Query query = Query.build();
        if (StringUtils.isNotEmpty(keyword)) {
            Disjunction disjunction = new Disjunction();
            disjunction.getChildren().add(new Filter(Filters.SOCIAL_REASON, keyword, Filter.LIKE));
            disjunction.getChildren().add(new Filter(Filters.NAME, keyword, Filter.LIKE));
            disjunction.getChildren().add(new Filter(Filters.LAST_NAME, keyword, Filter.LIKE));
            query.getFilters().add(disjunction);
        }
        return Repo.of(Customer.class).find(query).getRows();
    }

    @Override
    public Result<Customer> findCustomerByQuery(Query query) {
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
}
