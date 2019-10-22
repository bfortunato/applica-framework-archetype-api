package applica.api.domain.model.dossiers;

import applica.framework.AEntity;

public class SimulatedFinancing extends AEntity {
    private double twelvePaymentFee;
    private double twentyFourPaymentFee;
    private double thirtySixPaymentFee;
    private double fourtyEightPaymentFee;

    public SimulatedFinancing() {
    }

    public SimulatedFinancing(double twelvePaymentFee, double twentyFourPaymentFee, double thirtySixPaymentFee, double fourtyEightPaymentFee) {
        this.twelvePaymentFee = twelvePaymentFee;
        this.twentyFourPaymentFee = twentyFourPaymentFee;
        this.thirtySixPaymentFee = thirtySixPaymentFee;
        this.fourtyEightPaymentFee = fourtyEightPaymentFee;
    }

    public double getTwelvePaymentFee() {
        return twelvePaymentFee;
    }

    public void setTwelvePaymentFee(double twelvePaymentFee) {
        this.twelvePaymentFee = twelvePaymentFee;
    }

    public double getTwentyFourPaymentFee() {
        return twentyFourPaymentFee;
    }

    public void setTwentyFourPaymentFee(double twentyFourPaymentFee) {
        this.twentyFourPaymentFee = twentyFourPaymentFee;
    }

    public double getThirtySixPaymentFee() {
        return thirtySixPaymentFee;
    }

    public void setThirtySixPaymentFee(double thirtySixPaymentFee) {
        this.thirtySixPaymentFee = thirtySixPaymentFee;
    }

    public double getFourtyEightPaymentFee() {
        return fourtyEightPaymentFee;
    }

    public void setFourtyEightPaymentFee(double fourtyEightPaymentFee) {
        this.fourtyEightPaymentFee = fourtyEightPaymentFee;
    }
}
