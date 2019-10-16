package applica.api.domain.utils;

import applica.api.domain.model.dossiers.PriceCalculatorSheet;
import applica.api.domain.model.dossiers.RecommendedPrice;
import applica.api.domain.model.dossiers.ServiceCost;
import applica.api.domain.model.dossiers.SimulatedFinancing;

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
        return priceCalculatorSheet.getSignificantValue() >= (priceCalculatorSheet.getTotal()/2) ? priceCalculatorSheet.getSignificantValue() - priceCalculatorSheet.getNonSignificantValue() : 0;
    }

    private static double calculateDifferenceValueUnpacked(PriceCalculatorSheet priceCalculatorSheet, double significantValueUnpacked){
        return significantValueUnpacked == 0 ? priceCalculatorSheet.getSignificantValue() : priceCalculatorSheet.getSignificantValue() - priceCalculatorSheet.getNonSignificantValue();
    }

    private static double calculateTotalVatExcluded(PriceCalculatorSheet priceCalculatorSheet) {
        double significantValueUnpacked = calculateSignificantValueUnpacked(priceCalculatorSheet);
        double significantValueRecharged = significantValueUnpacked * (1 * RECHARGE);

        double differenceValueUnpacked = calculateDifferenceValueUnpacked(priceCalculatorSheet, significantValueUnpacked);
        double differenceValueRecharged = differenceValueUnpacked * (1 * RECHARGE);

        double nonSignificantValueUnpacked = priceCalculatorSheet.getNonSignificantValue() + priceCalculatorSheet.getServiceValue();
        double nonSignificantValueRecharged = nonSignificantValueUnpacked * (1 * RECHARGE);

        return significantValueRecharged + differenceValueRecharged + nonSignificantValueRecharged;
    }

    private static double calculateTotalVatIncluded(PriceCalculatorSheet priceCalculatorSheet) {
        double significantValueUnpacked = calculateSignificantValueUnpacked(priceCalculatorSheet);
        double significantValueRecharged = significantValueUnpacked * (1 * RECHARGE);
        double significantValueVAT = significantValueRecharged * VAT_22;

        double differenceValueUnpacked = calculateDifferenceValueUnpacked(priceCalculatorSheet, significantValueUnpacked);
        double differenceValueRecharged = differenceValueUnpacked * (1 * RECHARGE);
        double differenceValueVAT = differenceValueRecharged * VAT_10;

        double nonSignificantValueUnpacked = priceCalculatorSheet.getNonSignificantValue() + priceCalculatorSheet.getServiceValue();
        double nonSignificantValueRecharged = nonSignificantValueUnpacked * (1 * RECHARGE);
        double nonSignificantValueVAT = nonSignificantValueRecharged * VAT_10;

        double totalVATExcluded = significantValueRecharged + differenceValueRecharged + nonSignificantValueRecharged;
        double totalVAT = significantValueVAT + differenceValueVAT + nonSignificantValueVAT;

        return totalVATExcluded + totalVAT;
    }

    public static RecommendedPrice generateServiceRecommendedPrice(PriceCalculatorSheet priceCalculatorSheet) {
        return new RecommendedPrice(calculateTotalVatIncluded(priceCalculatorSheet));
    }

    public static ServiceCost generateServiceCost(PriceCalculatorSheet priceCalculatorSheet) {
        RecommendedPrice recommendedPrice = generateServiceRecommendedPrice(priceCalculatorSheet);
        double totalVATExcluded = calculateTotalVatExcluded(priceCalculatorSheet);
        double aggioTaxCredit = recommendedPrice.getNetAmountToBePaid() * AGGIO_TAX_CREDIT;
        double dimSolTaxCredit = totalVATExcluded * DIM_SOL_TAX_CREDIT;
        return new ServiceCost(recommendedPrice.getNetAmountToBePaid(), aggioTaxCredit + dimSolTaxCredit);
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
