package applica.api.domain.utils;

import applica.framework.Disjunction;
import applica.framework.Filter;
import applica.framework.Query;

public class FilterUtils {

    public static void addBooleanFilter(String filters, Query query) {
        Filter f = null;
        if (query.getFilterValue(filters).equals("true")){
            f = new Filter(filters, query.getFilterValue(filters).equals("true"), query.getFilterType(filters));
        } else {
            f = createBooleanFalseOrNotExistingFilter(filters);
        }
        query.getFilters().removeIf(fi -> fi.getProperty().equals(filters));
        query.getFilters().add(f);
    }

    public static Filter createBooleanFalseOrNotExistingFilter(String property) {
        Disjunction disjunction = new Disjunction();
        disjunction.getChildren().add(new Filter(property, false));
        disjunction.getChildren().add(new Filter(property, false, Filter.EXISTS));
        return disjunction;

    }
}
