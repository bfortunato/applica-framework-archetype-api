package applica.api.api.facade;

import applica.api.domain.model.User;
import applica.api.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountFacade {


    @Autowired
    private MailService mailService;

    public void sendRegistrationMail(User user, String tempPassword) {
        mailService.sendActivactionMail(user, tempPassword);
    }
}
