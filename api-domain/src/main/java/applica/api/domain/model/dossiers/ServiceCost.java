package applica.api.domain.model.dossiers;

import applica.framework.AEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ServiceCost extends AEntity {

    private double customerDeduction;
    private double initiativeCost;
    private double fabricatorPayOff;

    public ServiceCost(double customerDeduction, double initiativeCost) {
        this.customerDeduction = customerDeduction;
        this.initiativeCost = initiativeCost;
        BigDecimal bd = new BigDecimal(customerDeduction - initiativeCost);
        bd = bd.setScale(2, RoundingMode.HALF_EVEN);
        this.fabricatorPayOff = bd.doubleValue();
    }

    public double getCustomerDeduction() {
        return customerDeduction;
    }

    public void setCustomerDeduction(double customerDeduction) {
        this.customerDeduction = customerDeduction;
    }

    public double getInitiativeCost() {
        return initiativeCost;
    }

    public void setInitiativeCost(double initiativeCost) {
        this.initiativeCost = initiativeCost;
    }

    public double getFabricatorPayOff() {
        return fabricatorPayOff;
    }

    public void setFabricatorPayOff(double fabricatorPayOff) {
        this.fabricatorPayOff = fabricatorPayOff;
    }
}
