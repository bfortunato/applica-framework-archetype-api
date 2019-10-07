package applica.api.domain.model.auth;

import applica.api.domain.model.EntityList;
import applica.framework.AEntity;
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
public class User extends AEntity implements applica.framework.security.User {

    private String name;
    private String lastname;
    private String mail;
    private String password;
    private boolean active;
    private boolean firstLogin;
    private Date registrationDate;
    private String activationCode;
    private String image;
    private String coverImage;
    private Date lastLogin;
    private Date currentPasswordSetDate;

    @ManyToMany
    private List<Role> roles;

    private transient boolean needToChangePassword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
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