package applica.api.domain.model.users.categories;

import applica.api.domain.model.CodedEntity;

public class UsersCategory extends CodedEntity {

    private String description;
    private boolean active = true;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
