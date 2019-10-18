package applica.api.domain.model.dossiers;

import applica.framework.AEntity;

import java.util.Date;

public class PriceCalculatorSheet extends AEntity {

    private Date creationDate;
    private double significantValue;
    private double nonSignificantValue;
    private double serviceValue;
    private double total;

    public PriceCalculatorSheet() {}

    public PriceCalculatorSheet(double significantValue, double nonSignificantValue, double serviceValue) {
        this.significantValue = significantValue;
        this.nonSignificantValue = nonSignificantValue;
        this.serviceValue = serviceValue;
        this.creationDate = new Date();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public double getSignificantValue() {
        return significantValue;
    }

    public void setSignificantValue(double significantValue) {
        this.significantValue = significantValue;
    }

    public double getNonSignificantValue() {
        return nonSignificantValue;
    }

    public void setNonSignificantValue(double nonSignificantValue) {
        this.nonSignificantValue = nonSignificantValue;
    }

    public double getServiceValue() {
        return serviceValue;
    }

    public void setServiceValue(double serviceValue) {
        this.serviceValue = serviceValue;
    }

    public double getTotal() {
        return this.significantValue + this.nonSignificantValue;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
