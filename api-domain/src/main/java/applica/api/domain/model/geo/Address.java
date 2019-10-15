package applica.api.domain.model.geo;

import applica.framework.AEntity;

public class Address extends AEntity {

    GeoCity city;
    String address;
    String streetNumber;

    public GeoCity getCity() {
        return city;
    }

    public void setCity(GeoCity city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }
}
