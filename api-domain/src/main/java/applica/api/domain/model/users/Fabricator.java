package applica.api.domain.model.users;

import applica.api.domain.model.EntityList;
import applica.api.domain.model.dossiers.Document;
import applica.api.domain.model.geo.Address;
import applica.framework.widgets.entities.EntityId;

import java.util.ArrayList;
import java.util.List;

@EntityId(EntityList.FABRICATOR)
public class Fabricator extends Person {

    private String businessName;
    private Address address = new Address();
    private String referent;
    private String phoneNumber;

    private List<Document> documents = new ArrayList<>();

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getReferent() {
        return referent;
    }

    public void setReferent(String referent) {
        this.referent = referent;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
