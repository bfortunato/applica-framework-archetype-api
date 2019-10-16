package applica.api.domain.utils;

import applica.api.domain.model.auth.User;
import applica.framework.Repo;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

public class CustomUtils {

    public static User createUserFromPerson(ObjectNode node){
        User user = new User();

        user.setMail(node.get("mail").asText());
        user.setActive(true);
        user.setCurrentPasswordSetDate(new Date());
        user.setPassword(new BCryptPasswordEncoder().encode(node.get("password").asText()));
        user.setFirstLogin(true);
        user.setRegistrationDate(new Date());

        Repo.of(User.class).save(user);

        return user;
    }

}
