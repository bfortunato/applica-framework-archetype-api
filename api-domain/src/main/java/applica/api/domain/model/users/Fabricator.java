package applica.api.domain.model.users;

import applica.api.domain.model.EntityList;
import applica.api.domain.model.geo.Address;
import applica.framework.widgets.entities.EntityId;

@EntityId(EntityList.FABRICATOR)
public class Fabricator extends Person {

    private String businessName;
    private Address address = new Address();
    private String referent;

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
}
