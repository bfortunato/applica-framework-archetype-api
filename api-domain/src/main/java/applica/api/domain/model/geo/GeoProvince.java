package applica.api.domain.model.geo;

import applica.framework.annotations.ManyToOne;
import applica.framework.widgets.entities.EntityId;

/**
 * Created by federicomalvasi on 15/05/15.
 */

@EntityId("province")
public class GeoProvince extends GeoEntity {

    private String description;
    private String code;

    @ManyToOne
    private GeoRegion region;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String toString() {
        return String.format("%s (%s)", description, code);
    }

    public GeoRegion getRegion() {
        return region;
    }

    public void setRegion(GeoRegion region) {
        this.region = region;
    }
}
