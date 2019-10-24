package applica.api.domain.model.dossiers;

public class DossierCount {
    private long all;
    private long quotation;
    private long draft;
    private long tocandidate;
    private long candidated;
    private long approved;
    private long payoff;
    private long refused;

    public long getAll() {
        return all;
    }

    public void setAll(long all) {
        this.all = all;
    }

    public long getQuotation() {
        return quotation;
    }

    public void setQuotation(long quotation) {
        this.quotation = quotation;
    }

    public long getDraft() {
        return draft;
    }

    public void setDraft(long draft) {
        this.draft = draft;
    }

    public long getTocandidate() {
        return tocandidate;
    }

    public void setTocandidate(long tocandidate) {
        this.tocandidate = tocandidate;
    }

    public long getCandidated() {
        return candidated;
    }

    public void setCandidated(long candidated) {
        this.candidated = candidated;
    }

    public long getApproved() {
        return approved;
    }

    public void setApproved(long approved) {
        this.approved = approved;
    }

    public long getPayoff() {
        return payoff;
    }

    public void setPayoff(long payoff) {
        this.payoff = payoff;
    }

    public long getRefused() {
        return refused;
    }

    public void setRefused(long refused) {
        this.refused = refused;
    }
}
