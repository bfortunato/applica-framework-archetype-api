package applica.api.services;

import applica.api.domain.model.auth.User;
import applica.api.services.exceptions.UserAlreadyExistException;
import applica.framework.Query;
import applica.framework.Result;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public interface UserService {
    Result<User> getUserByPermission(List<String> permissions, Query query);

    List<User> findUsers(Query query);

    User getUser(String userId);

    List<User> getUserByIds(List<String> userIds);

    List<User> getUserByMails(List<String> mails);

    User getUserThatCanBeLoggedId(String authorId);

    User createUser(String mail, String password, String name, String lastname) throws UserAlreadyExistException;

    void updateUserIfNecessary(User user, ObjectNode node) throws UserAlreadyExistException;

    void updateUserIfNecessary(User user, String mail, String password, boolean active) throws UserAlreadyExistException;
}
