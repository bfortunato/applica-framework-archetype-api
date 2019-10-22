package applica.api.domain.model.dossiers;

import applica.api.domain.model.CodedEntity;
import applica.api.domain.model.EntityList;
import applica.api.domain.model.users.Customer;
import applica.framework.widgets.entities.EntityId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EntityId(EntityList.DOSSIER)
public class Dossier extends CodedEntity {

    public static final String STATUS_QUOTATION = "quotation";
    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_TO_CANDIDATE = "to-candidate";
    public static final String STATUS_CANDIDATED = "candidated";
    public static final String STATUS_REFUSED = "refused";
    public static final String STATUS_APPROVED = "approved";
    public static final String STATUS_PAY_OFF = "pay-off";

    private Object customerId;
    private Object fabricatorId;

    private transient Customer customer;

    private Date creationDate;
    private String status;

    private List<Document> documents = new ArrayList<>();
    private PriceCalculatorSheet priceCalculatorSheet = new PriceCalculatorSheet();
    private ServiceCost serviceCost;
    private RecommendedPrice recommendedPrice;
    private SimulatedFinancing simulatedFinancing;

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

    public ServiceCost getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(ServiceCost serviceCost) {
        this.serviceCost = serviceCost;
    }

    public RecommendedPrice getRecommendedPrice() {
        return recommendedPrice;
    }

    public void setRecommendedPrice(RecommendedPrice recommendedPrice) {
        this.recommendedPrice = recommendedPrice;
    }

    public SimulatedFinancing getSimulatedFinancing() {
        return simulatedFinancing;
    }

    public void setSimulatedFinancing(SimulatedFinancing simulatedFinancing) {
        this.simulatedFinancing = simulatedFinancing;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}
