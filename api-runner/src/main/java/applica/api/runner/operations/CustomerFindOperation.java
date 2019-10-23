package applica.api.runner.operations;

import applica.api.domain.model.Filters;
import applica.api.domain.model.users.Customer;
import applica.api.domain.utils.FilterUtils;
import applica.api.services.CustomersService;
import applica.framework.*;
import applica.framework.widgets.operations.BaseFindOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerFindOperation extends BaseFindOperation {

    @Autowired
    private CustomersService customersService;

    @Override
    public Class<? extends Entity> getEntityType() {
        return Customer.class;
    }



    @Override
    protected Result<? extends Entity> fetch(Query query) {
        if (query.hasFilter(Filters.ACTIVE)) {
            FilterUtils.addActiveFilter(query);
        }
        if (query.hasFilter(Filters.NAME)){
            Disjunction disjunction = new Disjunction();
            disjunction.getChildren().add(new Filter(Filters.SOCIAL_REASON, query.getFilterValue(Filters.NAME), Filter.LIKE));
            disjunction.getChildren().add(new Filter(Filters.FIRST_NAME, query.getFilterValue(Filters.NAME), Filter.LIKE));
            disjunction.getChildren().add(new Filter(Filters.LASTNAME, query.getFilterValue(Filters.NAME), Filter.LIKE));
            query.getFilters().removeIf(fi -> fi.getProperty().equals(Filters.NAME));
            query.getFilters().add(disjunction);
        }
        return customersService.findCustomerByQuery(query);
    }

    @Override
    public void onSerializeEntity(ObjectNode node, Entity entity) {
        Customer customer = ((Customer) entity);
    }
}
