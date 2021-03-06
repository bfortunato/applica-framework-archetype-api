package applica.api.services.impl;

import applica.api.domain.data.RolesRepository;
import applica.api.domain.data.UsersRepository;
import applica.api.domain.model.Filters;
import applica.api.domain.model.auth.*;
import applica.api.domain.utils.CustomLocalizationUtils;
import applica.api.services.AccountService;
import applica.api.services.MailService;
import applica.api.services.UserService;
import applica.api.services.exceptions.*;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.fileserver.FileServer;
import applica.framework.library.base64.URLData;
import applica.framework.library.mail.MailUtils;
import applica.framework.library.mail.Recipient;
import applica.framework.library.mail.TemplatedMail;
import applica.framework.library.options.OptionsManager;
import applica.framework.library.validation.Validation;
import applica.framework.library.validation.ValidationException;
import applica.framework.security.PasswordUtils;
import applica.framework.security.Security;
import applica.framework.security.SecurityUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static applica.api.domain.model.Filters.CODE;
import static applica.api.domain.model.Filters.USER_MAIL;
import static applica.api.domain.model.auth.PasswordReset.generatePasswordRequest;

/**
 * Created by bimbobruno on 15/11/2016.
 */
@Service
public class AccountServiceImpl implements AccountService {

    private Log logger = LogFactory.getLog(getClass());

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private OptionsManager options;

    @Autowired
    private FileServer fileServer;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;



    @Override
    public void register(String name, String email, String password) throws MailAlreadyExistsException, MailNotValidException, PasswordNotValidException, ValidationException {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
            throw new ValidationException(null);
        }

        final String mail = email.trim().toLowerCase();

        //check user existance
        if (usersRepository.find(Query.build().eq(USER_MAIL, mail)).findFirst().isPresent()) {
            throw new MailAlreadyExistsException();
        }

        if (!MailUtils.isValid(mail)) {
            throw new MailNotValidException();
        }

        if (!PasswordUtils.isValid(password)) {
            throw new PasswordNotValidException();
        }

        String activationCode = UUID.randomUUID().toString();
        String encodedPassword = SecurityUtils.encryptAndGetPassword(password);

        User user = new User();
        user.setName(name);
        user.setMail(mail);
        user.setPassword(encodedPassword);
        user.setActivationCode(activationCode);
        user.setActive(false);
        user.setRegistrationDate(new Date());

        Role role = getOrCreateRole(Role.USER);
        user.setRoles(Collections.singletonList(role));

        usersRepository.save(user);

        String activationUrl = String.format("%s/#/confirm?activationCode=%s", options.get("frontend.public.url"), user.getActivationCode());
        String loginUrl = String.format("%s/#/login", options.get("frontend.public.url"));
        final TemplatedMail templatedMail = new TemplatedMail();
        templatedMail.setOptions(options);
        templatedMail.setMailFormat(TemplatedMail.HTML);
        templatedMail.setTemplatePath("mailTemplates/register.vm");
        templatedMail.setFrom(options.get("registration.mail.from"));
        templatedMail.setSubject(options.get("registration.mail.subject"));
        templatedMail.setTo(mail);
        templatedMail.put("password", password);
        templatedMail.put("mail", mail);
        templatedMail.put("activationUrl", activationUrl);
        templatedMail.put("loginUrl", loginUrl);

        mailService.sendMail(templatedMail, Collections.singletonList(new Recipient(mail, Recipient.TYPE_TO)));
    }

    @Override
    public void confirm(String activationCode) throws MailNotFoundException {
        User user = usersRepository.find(Query.build().eq(Filters.USER_ACTIVATION_CODE, activationCode)).findFirst().orElseThrow(MailNotFoundException::new);
        user.setActive(true);
        user.setFirstLogin(true);
        user.setActivationCode(null);
        user.setActivationDate(new Date());
        usersRepository.save(user);
    }


    @Override
    public void recover(String mail) throws MailNotFoundException {
        User user = usersRepository.find(Query.build().eq(USER_MAIL, mail)).findFirst().orElseThrow(MailNotFoundException::new);

        String newPassword = PasswordUtils.generateRandom();
        String encodedPassword = SecurityUtils.encryptAndGetPassword(newPassword);
        user.setPassword(encodedPassword);

        TemplatedMail templatedMail = new TemplatedMail();
        templatedMail.setOptions(options);
        templatedMail.setMailFormat(TemplatedMail.HTML);
        templatedMail.setTemplatePath("mailTemplates/recover.vm");
        templatedMail.setFrom(options.get("registration.mail.from"));
        templatedMail.setSubject(options.get("registration.mail.subject"));
        templatedMail.setTo(mail);
        templatedMail.put("password", newPassword);
        templatedMail.put("mail", mail);

        mailService.sendMail(templatedMail, Collections.singletonList(new Recipient(mail, Recipient.TYPE_TO)));
    }


    @Override
    public URLData getProfileImage(Object userId, String size) throws UserNotFoundException, IOException {
        User user = usersRepository.get(userId).orElseThrow(UserNotFoundException::new);
        if (StringUtils.isNoneEmpty(user.getImage())) {
            InputStream in = fileServer.getImage(user.getImage(), size);
            return new URLData(String.format("image/%s", FilenameUtils.getExtension(user.getImage())), in);
        }

        return null;
    }

    @Override
    public void delete(Object id) throws UserNotFoundException {
        usersRepository.delete(id);
    }


    private Role getOrCreateRole(String roleName) {
        return rolesRepository.find(Query.build().filter(Filters.ROLE_NAME, roleName))
                .findFirst()
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setRole(roleName);
                    rolesRepository.save(newRole);
                    return newRole;
                });
    }


    @Override
    public void changePassword(User user, String currentPassword, String password, String passwordConfirm, boolean force) throws ValidationException {
        if (!force)
            Validation.validate(generatePasswordRequest(user, currentPassword, password, passwordConfirm));



        //Salvo la vecchia password (criptata) nello storico di quelle modificate dall'utente
        String previousPassword = user.getPassword();

        user.setCurrentPasswordSetDate(new Date());
        user.setPassword(SecurityUtils.encryptAndGetPassword(password));
        usersRepository.save(user);

        new Thread(() -> Repo.of(UserPassword.class).save(new UserPassword(previousPassword, user.getSid()))).start();
    }

    @Override
    public void changePassword(User user, String currentPassword, String password, String passwordConfirm) throws ValidationException {
        changePassword(user, currentPassword, password, passwordConfirm, false);
    }

    @Override
    public void deactivateInactiveUsers() {
        Calendar sixMonthAgo = Calendar.getInstance();
        //TODO: parametrizzare questo valore?
        sixMonthAgo.add(Calendar.MONTH, -6);
        Repo.of(User.class).find(Query.build().lte(Filters.LAST_LOGIN, sixMonthAgo.getTime())).getRows().forEach(u -> {
            u.setActive(false);
            Repo.of(User.class).save(u);
        });
    }

    @Override
    public boolean needToChangePassword(applica.framework.security.User user) {
        Calendar threeMonthAgo = Calendar.getInstance();
        threeMonthAgo.add(Calendar.MONTH, -1 * getPasswordDuration());
        return ((User) user).getCurrentPasswordSetDate() == null || ((User) user).getCurrentPasswordSetDate().before(threeMonthAgo.getTime());
    }

    private int getPasswordDuration() {
        return Integer.parseInt(options.get("password.duration"));
    }

    @Override
    public boolean hasPasswordSetBefore(Object userId, String md5Password, Integer changesToConsider) {
        Query query = Query.build().eq(Filters.USER_ID, userId).sort(Filters.CREATION_DATE, true);
        if (changesToConsider != null) {
            query.setPage(1);
            query.setRowsPerPage(changesToConsider);
        }

        return Repo.of(UserPassword.class).find(query).getRows().stream().filter(p -> Objects.equals(md5Password, p.getPassword())).collect(Collectors.toList()).size() > 0;
    }

    @Override
    public boolean isCurrentPassword(String password) {
        User user = (User) Security.withMe().getLoggedUser();
        try {
            return user != null && Objects.equals(SecurityUtils.encryptAndGetPassword(password), user.getPassword());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public PasswordRecoveryCode getPasswordRecoverForUser(String userId) {
        return Repo.of(PasswordRecoveryCode.class).find(Query.build().eq(Filters.USER_ID, userId)).findFirst().orElse(null);
    }

    @Override
    public PasswordRecoveryCode getPasswordRecoveryCode(String code) {
        return Repo.of(PasswordRecoveryCode.class).find(Query.build().eq(CODE, code)).findFirst().orElse(null);
    }

    @Override
    public void deletePasswordRecoveryCode(PasswordRecoveryCode code) {
        Repo.of(PasswordRecoveryCode.class).delete(code.getSid());
    }

    @Override
    public void savePasswordRecoveryCode(PasswordRecoveryCode passwordRecoveryCode) {
        Repo.of(PasswordRecoveryCode.class).save(passwordRecoveryCode);
    }

    @Override
    public void sendConfirmationCode(String mail) {
        User user = usersRepository.find(Query.build().eq(USER_MAIL, mail)).findFirst().orElse(null);
        if (user != null) {

            PasswordRecoveryCode passwordRecoveryCode = getPasswordRecoverForUser(user.getSid());

            if (passwordRecoveryCode == null) {
                passwordRecoveryCode = new PasswordRecoveryCode();
                passwordRecoveryCode.setUserId(user.getSid());
            }

            String code = randomAlphaNumeric(ThreadLocalRandom.current().nextInt(9, 15));
            passwordRecoveryCode.setCode(code);

            savePasswordRecoveryCode(passwordRecoveryCode);
            PasswordRecoveryCode finalPasswordRecoveryCode = passwordRecoveryCode;
            new Thread(() -> sendPasswordRecoveryCodeMail(user.getMail(), user.getFullName(), finalPasswordRecoveryCode)).start();

        }
    }

    private static String randomAlphaNumeric(int count) {
        String ALPHA_NUMERIC_STRING = "abcdefghilmnopqrstuvzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    private void sendPasswordRecoveryCodeMail(String mail, String name, PasswordRecoveryCode passwordRecoveryCode) {

        String template = "mailTemplates/passwordRecoveryCode.vm";
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("code", passwordRecoveryCode.getCode());

        createAndSendMail(template, TemplatedMail.HTML, CustomLocalizationUtils.getInstance().getMessage("subject.password.recovery.code"), Arrays.asList(new Recipient(mail, Recipient.TYPE_TO)), data);

    }

    private void createAndSendMail(String templatePath, int mailType, String subject, List<Recipient> recipients, Map<String, Object> data) {

        TemplatedMail mail = new TemplatedMail();
        mail.setOptions(options);
        mail.setMailFormat(mailType);
        mail.setTemplatePath(templatePath);
        mail.setFrom(options.get("registration.mail.from"));
        mail.setSubject(subject);
        mail.setRecipients(recipients);
        for (String key : data.keySet()) {
            mail.put(key, data.get(key));
        }

        mailService.sendMail(mail, recipients);

    }

    @Override
    public void validateRecoveryCode(String mail, String code, boolean deleteRecord, boolean propagateError) throws MailNotFoundException, CodeNotValidException {
        User user = usersRepository.find(Query.build().eq(USER_MAIL, mail.toLowerCase())).findFirst().orElse(null);
        if (user != null) {
            PasswordRecoveryCode passwordRecoveryCode = Repo.of(PasswordRecoveryCode.class).find(Query.build().eq(Filters.USER_ID, user.getSid()).eq(CODE, code)).findFirst().orElse(null);
            if (passwordRecoveryCode != null) {
                if (deleteRecord)
                    Repo.of(PasswordRecoveryCode.class).delete(passwordRecoveryCode.getId());
            } else if (propagateError)
                throw new CodeNotValidException();

        } else if (propagateError)
            throw new MailNotFoundException();

    }


    @Override
    public void resetPassword(String mail, String code, String password, String passwordConfirm) throws MailNotFoundException, CodeNotValidException, ValidationException {

        mail = mail.toLowerCase();
        validateRecoveryCode(mail, code, false, true);
        User user = userService.getUserByMails(Arrays.asList(mail)).get(0);
        changePassword(user, null, password, passwordConfirm);
        PasswordRecoveryCode passwordRecoveryCode = getPasswordRecoveryCode(code);
        if (passwordRecoveryCode != null)
            deletePasswordRecoveryCode(passwordRecoveryCode);
    }


    @Override
    public String generateOneTimePassword() {
        String tempPassword = options.get("password.onetime.value");
        if (!org.springframework.util.StringUtils.hasLength(tempPassword))
            tempPassword = UUID.randomUUID().toString().substring(0, 8).trim();
        return tempPassword;
    }
}
