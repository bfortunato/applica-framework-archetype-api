package applica.api.domain.model.auth;

import applica.framework.AEntity;

/**
 * Created by antoniolovicario on 06/11/17.
 */
public class PasswordChange extends AEntity{

    private User user;
    private String password;
    private String currentPassword;
    private String passwordConfirm;

    public PasswordChange(User user, String currentEncodedPassword, String password, String passwordConfirm) {
        this.user = user;
        this.currentPassword = currentEncodedPassword;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }

    public PasswordChange() {}


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
}
