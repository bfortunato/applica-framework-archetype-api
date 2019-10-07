package applica.api.domain.model.users;

import applica.api.domain.model.geo.Address;
import applica.framework.AEntity;

import java.util.Date;

public class Person extends AEntity {

    public static final String SEX_MALE = "M";
    public static final String SEX_FEMALE = "F";

    public static final String TYPE_CUSTOMER = "customer";
    public static final String TYPE_FABRICATOR = "fabricator";
    public static final String TYPE_ADMIN = "admin";

    public static final String SUBJECT_TYPE_PHYSICAL_PERSON = "physical-person";
    public static final String SUBJECT_TYPE_LEGAL_PERSON = "legal-person";

    String userId; //authentication enabled user

    private String type; //person type
    private boolean active;

    private String subjectType;
    private String firstName;
    private String lastName;
    private String sex;
    private Date birthDate;
    private String birthMunicipality;
    private Address address = new Address();
    private String socialReason;
    private boolean vatCodeEnabled;
    private String vatCode;

    private String notes;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthMunicipality() {
        return birthMunicipality;
    }

    public void setBirthMunicipality(String birthMunicipality) {
        this.birthMunicipality = birthMunicipality;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getSocialReason() {
        return socialReason;
    }

    public void setSocialReason(String socialReason) {
        this.socialReason = socialReason;
    }

    public boolean isVatCodeEnabled() {
        return vatCodeEnabled;
    }

    public void setVatCodeEnabled(boolean vatCodeEnabled) {
        this.vatCodeEnabled = vatCodeEnabled;
    }

    public String getVatCode() {
        return vatCode;
    }

    public void setVatCode(String vatCode) {
        this.vatCode = vatCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
