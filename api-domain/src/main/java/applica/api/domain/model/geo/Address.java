package applica.api.domain.model.geo;

import applica.framework.AEntity;

public class Address extends AEntity {

    GeoCity city;
    String address;
    String streetNumber;
    String fiscalCode;
    String phoneNumber;

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

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
