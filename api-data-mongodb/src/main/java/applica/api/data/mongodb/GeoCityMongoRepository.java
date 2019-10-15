package applica.api.data.mongodb;

import applica.api.domain.data.GeoCityRepository;
import applica.api.domain.model.Filters;
import applica.api.domain.model.geo.GeoCity;
import applica.framework.Query;
import applica.framework.data.mongodb.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public class GeoCityMongoRepository extends MongoRepository<GeoCity> implements GeoCityRepository {


    @Override
    public Class<GeoCity> getEntityType() {
        return GeoCity.class;
    }

    @Override
    public GeoCity getByPostalCode(String cap, Boolean isRepo) {
        try {
            return (GeoCity) this
                    .find(Query.build().eq(isRepo ? Filters.CAP : Filters.POSTAL_CODE, cap))
                    .findFirst()
                    .orElse(null);
        } catch(Throwable t) {
            t.printStackTrace();
        }

        return null;
    }
}
