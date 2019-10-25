package applica.api.runner.controllers;

import applica.api.domain.model.UserChangePasswordAttempt;
import applica.api.domain.utils.CustomDateUtils;
import applica.api.domain.utils.CustomErrorUtils;
import applica.api.domain.utils.CustomLocalizationUtils;
import applica.api.domain.utils.SecurityUtils;
import applica.api.runner.facade.AccountFacade;
import applica.api.runner.viewmodels.UIUserWithToken;
import applica.api.services.AccountService;
import applica.api.services.AuthService;
import applica.api.services.UserService;
import applica.api.services.exceptions.*;
import applica.api.services.responses.ResponseCode;
import applica.framework.library.base64.URLData;
import applica.framework.library.i18n.LocalizationUtils;
import applica.framework.library.responses.Response;
import applica.framework.library.responses.ValueResponse;
import applica.framework.library.validation.ValidationException;
import applica.framework.security.Security;
import applica.framework.security.User;
import applica.framework.security.authorization.AuthorizationException;
import applica.framework.security.token.TokenGenerationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

import static applica.api.domain.model.UserAttempt.WAITING_TIME_IN_SECONDS;
import static applica.api.services.responses.ResponseCode.*;
import static applica.framework.library.responses.Response.ERROR;
import static applica.framework.library.responses.Response.OK;

/**
 * Applica (www.applicadoit.com)
 * User: bimbobruno
 * Date: 4/17/13
 * Time: 5:47 PM
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountFacade accountFacade;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Response register(String name, String mail, String password) {
        try {
            var activationCode = accountService.register(name, mail, password);
            return new ValueResponse(activationCode);
        } catch (MailAlreadyExistsException e) {
            return new Response(ERROR_MAIL_ALREADY_EXISTS);
        } catch (MailNotValidException e) {
            return new Response(ERROR_MAIL_NOT_VALID);
        } catch (PasswordNotValidException e) {
            return new Response(ERROR_PASSWORD_NOT_VALID);
        } catch (ValidationException e) {
            return new Response(ERROR_VALIDATION);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }

    @PostMapping("/confirm")
    public Response confirm(String activationCode) {
        try {
            accountService.confirm(activationCode);
            return new Response(OK);
        } catch (MailNotFoundException e) {
            return new Response(ERROR_MAIL_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }

    @PostMapping("/recover")
    public Response recover(String mail) {
        try {
            accountService.recover(mail);
            return new Response(OK);
        } catch (MailNotFoundException e) {
            return new Response(ERROR_MAIL_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }

    @GetMapping("/{userId}/profile/image")
    public Response image(@PathVariable String userId) {
        try {
            URLData profileImage = accountService.getProfileImage(userId, "100x100");
            if (profileImage != null) {
                return new ValueResponse(profileImage.write());
            } else {
                return new Response(ERROR_NOT_FOUND);
            }
        } catch (UserNotFoundException e) {
            return new Response(ResponseCode.ERROR_USER_NOT_FOUND);
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(Response.ERROR);
        }
    }



    @RequestMapping("/changePassword")
    public @ResponseBody
    Response resetPassword(String currentPassword, String password, String passwordConfirm) {

        try {
            accountService.changePassword((applica.api.domain.model.auth.User) Security.withMe().getLoggedUser(), StringUtils.hasLength(currentPassword) ? SecurityUtils.encodePassword(currentPassword) : null, password, passwordConfirm);
        } catch (ValidationException e) {
            e.getValidationResult().getErrors();
            return new Response(Response.ERROR, CustomErrorUtils.getInstance().getAllErrorMessages(e.getValidationResult().getErrors()));
        } catch (MailNotFoundException e) {
            return new Response(ERROR, e.getMessage());
        } catch (Exception e) {
            return new Response(Response.ERROR, CustomErrorUtils.getInstance().getMessage("generic.error"));
        }
        User user = Security.withMe().getLoggedUser();
        String token = null;
        try {
            token = authService.token(((applica.api.domain.model.auth.User) user).getMail(), password);
        } catch (BadCredentialsException | TokenGenerationException | UserLoginMaxAttemptsException e) {
            e.printStackTrace();
        }
        return new ValueResponse(new UIUserWithToken(user, token));

    }



    @PostMapping("/resetUserPassword")
    public @ResponseBody
    Response resetUserPassword(String id) {
        try {

            String newPassword = accountFacade.generateAndSendUserOneTimePassword(id);
            if (Security.withMe().getLoggedUser().getId().equals(id)) {
                //Se sto modificando la mia stessa password dovrò aggiornare l'utenza sul client
                User user = Security.withMe().getLoggedUser();
                return new ValueResponse(new UIUserWithToken(user, authService.token(((applica.api.domain.model.auth.User) user).getMail(), newPassword)));
            }
        } catch (BadCredentialsException | AuthorizationException | TokenGenerationException e) {
            return new Response(ERROR, e.getMessage());
        } catch (Exception e) {
            return new Response(Response.ERROR, CustomErrorUtils.getInstance().getMessage("generic.error"));
        }
        return new Response(Response.OK);
    }
    @PostMapping("/resetPassword")
    public @ResponseBody
    Response reset(String mail, String code, String password, String passwordConfirm) {

        UserChangePasswordAttempt attempt = userService.getUserPasswordChangeAttempts(mail);
        if (attempt.isLocked())
            return new Response(Response.ERROR, CustomLocalizationUtils.getInstance().getMessage("error.maxLoginAttempts", String.valueOf(Math.abs(WAITING_TIME_IN_SECONDS - CustomDateUtils.getDifferenceInSeconds(attempt.getLastModified(), new Date())))));

        try {
            accountService.resetPassword(mail, code, password, passwordConfirm);
            userService.resetPasswordChangeFailAttempts(attempt);
        }  catch (ValidationException e) {
            e.getValidationResult().getErrors();
            userService.updatePasswordChangeFailAttempts(userService.getUserPasswordChangeAttempts(mail));
            return new Response(Response.ERROR, CustomErrorUtils.getInstance().getAllErrorMessages(e.getValidationResult().getErrors()));
        }  catch (Exception e) {
            userService.updatePasswordChangeFailAttempts(attempt);
            return new Response(Response.ERROR, CustomErrorUtils.getInstance().getMessage("generic.error"));
        }
        return new Response(Response.OK);
    }

    @PostMapping("/sendConfirmationCode")
    public Response sendConfirmationCode(String mail) {
        try {
            accountFacade.sendConfirmationCode(mail);
            return new Response(Response.OK);
        } catch (MailNotFoundException e) {
            return new Response(Response.ERROR, LocalizationUtils.getInstance().getMessage("error.mail.not.found"));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Response.ERROR);
        }
    }


    @PostMapping( "/validateRecoveryCode")
    public @ResponseBody
    Response validateRecoveryCode(String mail, String code) {

        UserChangePasswordAttempt attempt = userService.getUserPasswordChangeAttempts(mail);
        if (attempt.isLocked())
            return new Response(Response.ERROR, CustomLocalizationUtils.getInstance().getMessage("error.maxLoginAttempts", String.valueOf(Math.abs(WAITING_TIME_IN_SECONDS - CustomDateUtils.getDifferenceInSeconds(attempt.getLastModified(), new Date())))));

        try {
            accountService.validateRecoveryCode(mail, code, false, true);
            userService.resetPasswordChangeFailAttempts(attempt);
            return new Response(Response.OK);
        }  catch (Exception e) {
            userService.updatePasswordChangeFailAttempts(attempt);
            return new Response(Response.ERROR, CustomErrorUtils.getInstance().getMessage("msg.validation"));
        }
    }
}
