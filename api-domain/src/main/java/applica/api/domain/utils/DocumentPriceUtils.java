package applica.api.domain.utils;

import applica.api.domain.model.dossiers.PriceCalculatorSheet;
import applica.api.domain.model.dossiers.RecommendedPrice;
import applica.api.domain.model.dossiers.ServiceCost;
import applica.api.domain.model.dossiers.SimulatedFinancing;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DocumentPriceUtils {
    private static final int VAT_10 = 10;
    private static final int VAT_22 = 22;

    private static final double RECHARGE = 16.9;
    private static final double AGGIO_TAX_CREDIT = 20;
    private static final double DIM_SOL_TAX_CREDIT = 3;

    private static final double TWELVE_PAYMENT_FEE = 5;
    private static final double TWENTY_FOUR_PAYMENT_FEE = 5;
    private static final double THIRTY_SIX_PAYMENT_FEE = 5;
    private static final double FOURTY_EIGHT_PAYMENT_FEE = 5;

    private static double calculateSignificantValueUnpacked(PriceCalculatorSheet priceCalculatorSheet) {
        return priceCalculatorSheet.getSignificantValue() >= (priceCalculatorSheet.getTotal()/2) ? priceCalculatorSheet.getSignificantValue() - (priceCalculatorSheet.getNonSignificantValue() + priceCalculatorSheet.getServiceValue()) : 0;
    }

    private static double calculateDifferenceValueUnpacked(PriceCalculatorSheet priceCalculatorSheet, double significantValueUnpacked){
        return significantValueUnpacked == 0 ? priceCalculatorSheet.getSignificantValue() : priceCalculatorSheet.getSignificantValue() - significantValueUnpacked;
    }

    private static double calculateTotalVatExcluded(PriceCalculatorSheet priceCalculatorSheet) {
        double significantValueUnpacked = calculateSignificantValueUnpacked(priceCalculatorSheet);
        double significantValueRecharged = significantValueUnpacked + ((significantValueUnpacked * RECHARGE)/100);

        double differenceValueUnpacked = calculateDifferenceValueUnpacked(priceCalculatorSheet, significantValueUnpacked);
        double differenceValueRecharged = differenceValueUnpacked + ((differenceValueUnpacked * RECHARGE)/100);

        double nonSignificantValueUnpacked = priceCalculatorSheet.getNonSignificantValue() + priceCalculatorSheet.getServiceValue();
        double nonSignificantValueRecharged = nonSignificantValueUnpacked + ((nonSignificantValueUnpacked * RECHARGE)/100);

        return significantValueRecharged + differenceValueRecharged + nonSignificantValueRecharged;
    }

    private static double calculateTotalVatIncluded(PriceCalculatorSheet priceCalculatorSheet) {
        double significantValueUnpacked = calculateSignificantValueUnpacked(priceCalculatorSheet);
        double significantValueRecharged = significantValueUnpacked + ((significantValueUnpacked * RECHARGE)/100);

        BigDecimal bdSVT = new BigDecimal(Double.toString(((significantValueRecharged * VAT_22)/100)));
        bdSVT = bdSVT.setScale(2, RoundingMode.HALF_EVEN);
        double significantValueVAT =  bdSVT.doubleValue();

        double differenceValueUnpacked = calculateDifferenceValueUnpacked(priceCalculatorSheet, significantValueUnpacked);
        double differenceValueRecharged = differenceValueUnpacked + ((differenceValueUnpacked * RECHARGE)/100);

        BigDecimal bdDVT = new BigDecimal(Double.toString(((differenceValueRecharged * VAT_10)/100)));
        bdDVT = bdDVT.setScale(2, RoundingMode.HALF_EVEN);
        double differenceValueVAT  =  bdDVT.doubleValue();

        double nonSignificantValueUnpacked = priceCalculatorSheet.getNonSignificantValue() + priceCalculatorSheet.getServiceValue();
        double nonSignificantValueRecharged = nonSignificantValueUnpacked + ((nonSignificantValueUnpacked * RECHARGE)/100);

        BigDecimal bdNSV = new BigDecimal(Double.toString(((nonSignificantValueRecharged * VAT_10)/100)));
        bdNSV = bdNSV.setScale(2, RoundingMode.HALF_EVEN);
        double nonSignificantValueVAT  =  bdNSV.doubleValue();

        double totalVATExcluded = significantValueRecharged + differenceValueRecharged + nonSignificantValueRecharged;

        BigDecimal bdTotalVat = new BigDecimal(significantValueVAT + differenceValueVAT + nonSignificantValueVAT);
        bdTotalVat = bdTotalVat.setScale(2, RoundingMode.HALF_EVEN);
        double totalVAT  =  bdTotalVat.doubleValue();

        BigDecimal bdTotal = new BigDecimal(totalVATExcluded + totalVAT);
        bdTotal = bdTotal.setScale(2, RoundingMode.HALF_EVEN);
        return bdTotal.doubleValue();

    }

    public static RecommendedPrice generateServiceRecommendedPrice(PriceCalculatorSheet priceCalculatorSheet) {
        BigDecimal bd = new BigDecimal(calculateTotalVatIncluded(priceCalculatorSheet));
        bd = bd.setScale(2, RoundingMode.HALF_EVEN);
        return new RecommendedPrice(bd.doubleValue());
    }

    public static ServiceCost generateServiceCost(PriceCalculatorSheet priceCalculatorSheet) {
        RecommendedPrice recommendedPrice = generateServiceRecommendedPrice(priceCalculatorSheet);
        double totalVATExcluded = calculateTotalVatExcluded(priceCalculatorSheet);
        double aggioTaxCredit = (recommendedPrice.getNetAmountToBePaid() * AGGIO_TAX_CREDIT)/100;
        double dimSolTaxCredit = (totalVATExcluded * DIM_SOL_TAX_CREDIT)/100;
        BigDecimal bdCustomer = new BigDecimal(recommendedPrice.getNetAmountToBePaid());
        bdCustomer = bdCustomer.setScale(2, RoundingMode.HALF_EVEN);

        BigDecimal bdInitiative = new BigDecimal(aggioTaxCredit + dimSolTaxCredit);
        bdInitiative = bdInitiative.setScale(2, RoundingMode.HALF_EVEN);
        return new ServiceCost(bdCustomer.doubleValue(), bdInitiative.doubleValue());
    }

    public static SimulatedFinancing generateSimulatedFinancing(PriceCalculatorSheet priceCalculatorSheet) {
        RecommendedPrice recommendedPrice = generateServiceRecommendedPrice(priceCalculatorSheet);
        double total = recommendedPrice.getNetAmountToBePaid();
        double twelvePaymentFee = (total*(TWELVE_PAYMENT_FEE/100.0f));
        double twentyFourPaymentFee = (total*(TWENTY_FOUR_PAYMENT_FEE/100.0f));
        double thirtySixPaymentFee = (total*(THIRTY_SIX_PAYMENT_FEE/100.0f));
        double fourtyEightPaymentFee = (total*(FOURTY_EIGHT_PAYMENT_FEE/100.0f));
        return new SimulatedFinancing(twelvePaymentFee, twentyFourPaymentFee, thirtySixPaymentFee, fourtyEightPaymentFee);
    }
}
