package applica.api.domain.model.users;

import applica.api.domain.model.EntityList;
import applica.api.domain.model.dossiers.Document;
import applica.api.domain.model.geo.Address;
import applica.framework.widgets.entities.EntityId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EntityId(EntityList.FABRICATOR)
public class Fabricator extends Person {

    private String businessName;
    private Address address = new Address();
    private String referent;
    private String phoneNumber;

    private String name;
    private String lastname;
    private String birthPlace;
    private Date birthDate;
    private String fiscalCode;

    private boolean appEnabled;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public boolean isAppEnabled() {
        return appEnabled;
    }

    public void setAppEnabled(boolean appEnabled) {
        this.appEnabled = appEnabled;
    }
}
