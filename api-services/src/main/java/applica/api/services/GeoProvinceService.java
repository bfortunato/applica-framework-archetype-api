package applica.api.services;

import applica.api.domain.model.geo.GeoProvince;

public interface GeoProvinceService {

    GeoProvince getByCode(String code);

    void save(GeoProvince geoProvince);

}
