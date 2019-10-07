package applica.api.data.mongodb;

import applica.api.domain.model.UserDetails;
import applica.api.domain.data.UsersRepository;
import applica.api.domain.model.Filters;
import applica.framework.Query;
import applica.framework.security.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Applica (www.applica.guru)
 * User: bimbobruno
 * Date: 2/26/13
 * Time: 6:18 PM
 */
@Repository
public class UserDetailsMongoRepository implements UserDetailsRepository {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public org.springframework.security.core.userdetails.UserDetails getByMail(String mail) {
        try {
            return usersRepository
                    .find(Query.build().eq(Filters.USER_MAIL, mail))
                    .findFirst()
                    .map(UserDetails::new)
                    .orElse(null);
        } catch(Throwable t) {
            t.printStackTrace();
        }

        return null;
    }
}