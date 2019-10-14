package applica.api.domain.model.dossiers;

import applica.framework.AEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Dossier extends AEntity {

    public static final String STATUS_QUOTATION = "quotation";
    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_TO_VERIFY = "to-verify";
    public static final String STATUS_TO_CANDIDATE = "to-candidate";
    public static final String STATUS_CANDIDATED = "candidated";
    public static final String STATUS_REFUSED = "refused";
    public static final String STATUS_APPROVED = "approved";

    private Object customerId;
    private Object fabricatorId;

    private Date creationDate;
    private String status;

    private List<Document> documents = new ArrayList<>();
    private PriceCalculatorSheet priceCalculatorSheet = new PriceCalculatorSheet();

    public Object getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Object customerId) {
        this.customerId = customerId;
    }

    public Object getFabricatorId() {
        return fabricatorId;
    }

    public void setFabricatorId(Object fabricatorId) {
        this.fabricatorId = fabricatorId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public PriceCalculatorSheet getPriceCalculatorSheet() {
        return priceCalculatorSheet;
    }

    public void setPriceCalculatorSheet(PriceCalculatorSheet priceCalculatorSheet) {
        this.priceCalculatorSheet = priceCalculatorSheet;
    }
}
