package applica.api.domain.model.users;

import applica.api.domain.model.EntityList;
import applica.framework.widgets.entities.EntityId;

@EntityId(EntityList.ADMIN_USER)
public class AdminUser extends Person {

    private String name;
    private String lastname;
    private String phoneNumber;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
