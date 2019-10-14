package applica.api.domain.model.users;

import applica.api.domain.model.CodedEntity;

public class Person extends CodedEntity {

    public static final String SEX_MALE = "M";
    public static final String SEX_FEMALE = "F";

    public static final String TYPE_END_USER = "end-user";
    public static final String TYPE_FABRICATOR = "fabricator";
    public static final String TYPE_ADMIN = "admin";

    public static final String SUBJECT_TYPE_PHYSICAL_PERSON = "physical-person";
    public static final String SUBJECT_TYPE_LEGAL_PERSON = "legal-person";

    Object userId; //authentication enabled user
    Object ownerId; //if required, the person who created this one

    private String categoryId; //person type
    private boolean active;

    private String mail;
    private String password;

    private String notes;

    public Object getUserId() {
        return userId;
    }

    public void setUserId(Object userId) {
        this.userId = userId;
    }

    public Object getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Object ownerId) {
        this.ownerId = ownerId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
