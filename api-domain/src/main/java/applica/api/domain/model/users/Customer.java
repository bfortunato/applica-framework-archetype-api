package applica.api.domain.model.users;

import applica.api.domain.model.geo.Address;

import java.util.Date;

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
    private boolean vatCodeEnabled;
    private String vatCode;

}
