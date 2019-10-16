package applica.api.domain.model.dossiers;

public class RecommendedPrice {

    private double recommendedRetailPrice;
    private double discount;
    private double netAmountToBePaid;

    public RecommendedPrice(double recommendedRetailPrice) {
        this.recommendedRetailPrice = recommendedRetailPrice;
        this.discount = 50;
        this.netAmountToBePaid = recommendedRetailPrice/this.discount;
    }

    public double getRecommendedRetailPrice() {
        return recommendedRetailPrice;
    }

    public void setRecommendedRetailPrice(double recommendedRetailPrice) {
        this.recommendedRetailPrice = recommendedRetailPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getNetAmountToBePaid() {
        return netAmountToBePaid;
    }

    public void setNetAmountToBePaid(double netAmountToBePaid) {
        this.netAmountToBePaid = netAmountToBePaid;
    }
}
