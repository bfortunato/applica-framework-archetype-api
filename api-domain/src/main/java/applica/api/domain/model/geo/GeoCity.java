package applica.api.domain.model.geo;


import applica.framework.AEntity;

public class GeoCity extends AEntity {

    String description;
    String cap;

    GeoProvince province;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public GeoProvince getProvince() {
        return province;
    }

    public void setProvince(GeoProvince province) {
        this.province = province;
    }

    public String toString() {
        return String.format("%s (%s)", description, cap);
    }
}
