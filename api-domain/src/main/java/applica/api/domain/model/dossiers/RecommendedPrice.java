package applica.api.domain.model.dossiers;

import applica.framework.AEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RecommendedPrice extends AEntity {

    private double recommendedRetailPrice;
    private double discount;
    private double netAmountToBePaid;

    public RecommendedPrice() {
    }

    public RecommendedPrice(double recommendedRetailPrice) {
        this.recommendedRetailPrice = recommendedRetailPrice;
        this.discount = 50;
        BigDecimal bd = new BigDecimal(recommendedRetailPrice - (recommendedRetailPrice*this.discount)/100);
        bd = bd.setScale(2, RoundingMode.HALF_EVEN);
        this.netAmountToBePaid = bd.doubleValue();
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
