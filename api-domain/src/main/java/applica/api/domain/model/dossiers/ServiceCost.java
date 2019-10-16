package applica.api.domain.model.dossiers;

public class ServiceCost {

    private double customerDeduction;
    private double initiativeCost;
    private double fabricatorPayOff;

    public ServiceCost(double customerDeduction, double initiativeCost) {
        this.customerDeduction = customerDeduction;
        this.initiativeCost = initiativeCost;
        this.fabricatorPayOff = customerDeduction - initiativeCost;
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
