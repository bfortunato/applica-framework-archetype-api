package applica.api.services;

import applica.api.domain.model.auth.User;
import applica.framework.library.mail.Recipient;
import applica.framework.library.mail.TemplatedMail;

import java.util.List;
import java.util.Map;

public interface MailService {
    TemplatedMail createMail(String templatePath, int mailType, String subject, Map<String, Object> data);

    void sendMail(TemplatedMail mail, List<Recipient> recipients);

    void sendActivationMail(User user, String defaultPassword);

    void createAndSendMail(String template, int mailType, String message, List<Recipient> recipients, Map<String, Object> data);

    void sendSimpleMail(String mail, String text);
}
