package applica.api.domain.model.geo;


import applica.framework.AEntity;

public class GeoProvince extends AEntity {

    String description;
    String code;

    private boolean excludeInReport;

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
        return String.format("%s (%s)", description != null? description : "", code != null? code : "");
    }

    public boolean isExcludeInReport() {
        return excludeInReport;
    }

    public void setExcludeInReport(boolean excludeInReport) {
        this.excludeInReport = excludeInReport;
    }
}
