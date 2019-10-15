package applica.api.domain.model.users;

import applica.api.domain.model.EntityList;
import applica.api.domain.model.geo.Address;
import applica.framework.widgets.entities.EntityId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@EntityId(EntityList.CUSTOMER)
public class Customer extends Person {

    public static final String SEX_MALE = "M";
    public static final String SEX_FEMALE = "F";

    public static final String SUBJECT_TYPE_PHYSICAL_PERSON = "physical-person";
    public static final String SUBJECT_TYPE_LEGAL_PERSON = "legal-person";

    private String subjectType;
    private String firstName;
    private String lastName;
    private String sex;
    private Date birthDate;
    private Address birthAddress = new Address();
    private Address address = new Address();
    private String socialReason;
    private boolean vatCodeDisabled;
    private String vatCode;
    private String fiscalCode;
    private String phoneNumber;

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

    public Address getBirthAddress() {
        return birthAddress;
    }

    public void setBirthAddress(Address birthAddress) {
        this.birthAddress = birthAddress;
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

    public boolean isVatCodeDisabled() {
        return vatCodeDisabled;
    }

    public void setVatCodeDisabled(boolean vatCodeDisabled) {
        this.vatCodeDisabled = vatCodeDisabled;
    }

    public String getVatCode() {
        return vatCode;
    }

    public void setVatCode(String vatCode) {
        this.vatCode = vatCode;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
