package applica.api.services.impl;

import applica.api.data.mongodb.utils.RepositoryUtils;
import applica.api.domain.model.Filters;
import applica.api.domain.model.auth.Role;
import applica.api.domain.model.auth.User;
import applica.api.services.UserService;
import applica.api.services.exceptions.UserAlreadyExistException;
import applica.framework.*;
import applica.framework.security.Security;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public Result<User> getUserByPermission(List<String> permissions, Query query) {

        if (query == null)
            query = Query.build();

        List<Role> roles = Repo.of(Role.class).find(Query.build().in("permissions", permissions)).getRows();
        if (roles.size() > 0) {
            query.getFilters().add(new Filter(Filters.USER_ROLES_ID,roles.stream().map(AEntity::getSid).collect(Collectors.toList()), Filter.IN));
            return Repo.of(User.class).find(query);
        }
        return null;
    }

    @Override
    public List<User> findUsers(Query query) {
        return Repo.of(User.class).find(query).getRows();
    }

    @Override
    public User getUser(String userId) {
        return Repo.of(User.class).get(userId).orElse(null);
    }

    @Override
    public List<User> getUserByIds(List<String> userIds) {
        return Repo.of(User.class).find(Query.build().in(Filters.REPOSITORY_ID, RepositoryUtils.getRepositoryIdFromIds(userIds))).getRows();
    }

    @Override
    public List<User> getUserByMails(List<String> mails) {
        return Repo.of(User.class).find(Query.build().in(Filters.USER_MAIL, mails)).getRows();
    }

    @Override
    public User getUserThatCanBeLoggedId(String userId) {
        return ((User) Security.withMe().getLoggedUser()).getSid().equals(userId) ? (User) Security.withMe().getLoggedUser() : Repo.of(User.class).get(userId).orElse(null);
    }

    @Override
    public User createUser(String mail, String password, String name, String lastname) throws UserAlreadyExistException {
        List<User> users = getUserByMails(Collections.singletonList(mail));
        if (users.size() == 0){
            User user = new User();
            user.setName(name);
            user.setLastname(lastname);
            user.setMail(mail);
            user.setActive(true);
            user.setCurrentPasswordSetDate(new Date());
            user.setPassword(new BCryptPasswordEncoder().encode(password));
            user.setFirstLogin(false);
            user.setRegistrationDate(new Date());

            Repo.of(User.class).save(user);

            return user;
        } else throw new UserAlreadyExistException(mail);
    }

    @Override
    public void updateUserIfNecessary(User user, ObjectNode node) throws UserAlreadyExistException {
        boolean save = false;
        if (!Objects.equals(user.getMail(), node.get("mail").asText())){
            List<User> users = getUserByMails(Collections.singletonList(node.get("mail").asText()));
            if (users.size() == 0){
                user.setMail(node.get("mail").asText());
                save = true;
            } else throw new UserAlreadyExistException(node.get("mail").asText());
        }

        if (node.get("password") != null && !node.get("password").isNull() && !Objects.equals(user.getPassword(), node.get("password").asText())){
            save = true;
            user.setCurrentPasswordSetDate(new Date());
            user.setPassword(new BCryptPasswordEncoder().encode(node.get("password").asText()));
        }

        if (node.get("active").asBoolean() != user.isActive()){
            save = true;
            user.setActive(node.get("active").asBoolean());
        }

        if (save) {
            Repo.of(User.class).save(user);
        }
    }

    @Override
    public void updateUserIfNecessary(User user, String mail, String password, boolean active) throws UserAlreadyExistException {
        boolean save = false;
        if (!Objects.equals(user.getMail(), mail)){
            List<User> users = getUserByMails(Collections.singletonList(mail));
            if (users.size() == 0){
                user.setMail(mail);
                save = true;
            } else throw new UserAlreadyExistException(mail);
        }

        if (StringUtils.hasLength(password) && !Objects.equals(user.getPassword(), password)){
            save = true;
            user.setCurrentPasswordSetDate(new Date());
            user.setPassword(new BCryptPasswordEncoder().encode(password));
        }

        if (active != user.isActive()){
            save = true;
            user.setActive(active);
        }

        if (save) {
            Repo.of(User.class).save(user);
        }
    }
}
