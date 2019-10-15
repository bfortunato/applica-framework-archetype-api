package applica.api.domain.model.dossiers;

import applica.api.domain.model.users.Person;

import java.util.Date;
import java.util.Objects;

public class DossierWorkflow {

    private Dossier dossier;

    public DossierWorkflow() {

    }

    public DossierWorkflow(Dossier dossier) {

    }

    public void create(Person fabricator, Person customer, PriceCalculatorSheet priceCalculatorSheet) {
        var dossier = new Dossier();

        dossier.setCreationDate(new Date());
        dossier.setFabricatorId(fabricator.getId());
        dossier.setCustomerId(customer.getId());
        dossier.setStatus(Dossier.STATUS_QUOTATION);
        dossier.setPriceCalculatorSheet(priceCalculatorSheet);

        this.dossier = dossier;
    }

    /**
     *
     */
    public void confirmQuotation() {
        Objects.requireNonNull(dossier, "Dossier not loaded. Call DossierWorkflow(Dossier dossier) constructor");

        dossier.setStatus(Dossier.STATUS_DRAFT);
    }

    /**
     * Try to move a dossier from draft status to toVerify status
     */
    public void commit() {
        Objects.requireNonNull(dossier, "Dossier not loaded. Call DossierWorkflow(Dossier dossier) constructor");

        dossier.setStatus(Dossier.STATUS_TO_CANDIDATE);
    }

    public void candidate() {
        Objects.requireNonNull(dossier, "Dossier not loaded. Call DossierWorkflow(Dossier dossier) constructor");

        dossier.setStatus(Dossier.STATUS_CANDIDATED);
    }

    public void approve() {
        Objects.requireNonNull(dossier, "Dossier not loaded. Call DossierWorkflow(Dossier dossier) constructor");

        dossier.setStatus(Dossier.STATUS_APPROVED);
    }

    public void refuse() {
        Objects.requireNonNull(dossier, "Dossier not loaded. Call DossierWorkflow(Dossier dossier) constructor");

        dossier.setStatus(Dossier.STATUS_REFUSED);
    }

    public void payOff() {
        Objects.requireNonNull(dossier, "Dossier not loaded. Call DossierWorkflow(Dossier dossier) constructor");

        dossier.setStatus(Dossier.STATUS_PAY_OFF);
    }

}
