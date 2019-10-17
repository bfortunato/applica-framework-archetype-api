package applica.api.domain.utils;

import applica.api.domain.model.Filters;
import applica.framework.Filter;
import applica.framework.Query;

public class FilterUtils {

    public static void addActiveFilter(Query query) {
        Filter f = new Filter(Filters.ACTIVE, query.getFilterValue(Filters.ACTIVE).equals("true"), query.getFilterType(Filters.ACTIVE));
        query.getFilters().removeIf(fi -> fi.getProperty().equals(Filters.ACTIVE));
        query.getFilters().add(f);
    }
}
