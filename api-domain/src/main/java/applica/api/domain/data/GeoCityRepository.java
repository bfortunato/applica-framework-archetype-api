package applica.api.domain.data;

import applica.api.domain.model.geo.GeoCity;
import applica.framework.Repository;

public interface GeoCityRepository extends Repository<GeoCity> {

    GeoCity getByPostalCode(String cap, Boolean isRepo);

}
