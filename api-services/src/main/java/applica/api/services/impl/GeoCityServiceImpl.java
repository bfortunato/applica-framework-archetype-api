package applica.api.services.impl;

import applica.framework.Query;
import applica.framework.Result;
import applica.api.domain.data.GeoCityRepository;
import applica.api.domain.model.geo.GeoCity;
import applica.api.services.GeoCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeoCityServiceImpl implements GeoCityService {

    @Autowired
    private GeoCityRepository geoCityMongoRepository;

    @Override
    public GeoCity getByPostalCode(String cap) {
        return geoCityMongoRepository.getByPostalCode(cap, false);
    }

    @Override
    public void save(GeoCity geoCity) {
        geoCityMongoRepository.save(geoCity);
    }

    @Override
    public Result<GeoCity> filterByQuery(Query query) {
        return geoCityMongoRepository.find(query);
    }
}
