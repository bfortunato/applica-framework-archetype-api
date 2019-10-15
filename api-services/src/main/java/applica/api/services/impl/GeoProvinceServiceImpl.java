package applica.api.services.impl;

import applica.api.data.mongodb.GeoProvinceMongoRepository;
import applica.api.domain.model.geo.GeoProvince;
import applica.api.services.GeoProvinceService;
import applica.framework.Query;
import applica.framework.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeoProvinceServiceImpl implements GeoProvinceService {

    @Autowired
    private GeoProvinceMongoRepository geoProvinceMongoRepository;

    @Override
    public GeoProvince getByCode(String cap) {
        return geoProvinceMongoRepository.getByCode(cap);
    }

    @Override
    public void save(GeoProvince geoProvince) {
        geoProvinceMongoRepository.save(geoProvince);
    }

    @Override
    public Result<GeoProvince> filterByQuery(Query query) {
        return geoProvinceMongoRepository.find(query);
    }

}
