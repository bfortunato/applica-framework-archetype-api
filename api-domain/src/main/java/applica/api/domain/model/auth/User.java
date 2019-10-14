package applica.api.domain.model.auth;

import applica.api.domain.model.EntityList;
import applica.api.domain.model.users.Person;
import applica.framework.annotations.ManyToMany;
import applica.framework.widgets.entities.EntityId;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Applica (www.applica.guru)
 * User: bimbobruno
 * Date: 6/12/2016
 * Time: 17:08
 */
@EntityId(EntityList.USER)
public class User extends Person implements applica.framework.security.User {

    private String name;
    private String lastname;
    private boolean firstLogin;
    private Date registrationDate;
    private String activationCode;
    private String image;
    private Date lastLogin;
    private Date currentPasswordSetDate;
    private String phoneNumber;

    @ManyToMany
    private List<Role> roles;

    private transient boolean needToChangePassword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public Date getCurrentPasswordSetDate() {
        return currentPasswordSetDate;
    }

    public void setCurrentPasswordSetDate(Date currentPasswordSetDate) {
        this.currentPasswordSetDate = currentPasswordSetDate;
    }

    public String getFullName() {
        return String.format("%s %s", lastname, name);
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public boolean isNeedToChangePassword() {
        return needToChangePassword;
    }

    public void setNeedToChangePassword(boolean needToChangePassword) {
        this.needToChangePassword = needToChangePassword;
    }

    public String getLastname() {
        return lastname;
    }

    public String getInitials() {
        if (StringUtils.hasLength(getMail())) {
            return getMail().substring(0, 1);
        }

        return "@";
    }

    @Override
    public String toString() {
        return getMail();
    }

    @Override
    public String getUsername() {
        return getMail();
    }
}
