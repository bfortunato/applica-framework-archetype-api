package applica.api.domain.utils;

import applica.api.domain.model.auth.User;
import applica.framework.Repo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

public class CustomUtils {

    public static User createUser(String mail, String password, String name, String lastname){
        User user = new User();
        user.setName(name);
        user.setLastname(lastname);
        user.setMail(mail);
        user.setActive(true);
        user.setCurrentPasswordSetDate(new Date());
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setFirstLogin(true);
        user.setRegistrationDate(new Date());

        Repo.of(User.class).save(user);

        return user;
    }

}
