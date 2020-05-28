package applica.api.runner.controllers;

import applica.api.domain.model.UserChangePasswordAttempt;
import applica.api.domain.utils.CustomDateUtils;
import applica.api.domain.utils.CustomErrorUtils;
import applica.api.domain.utils.CustomLocalizationUtils;
import applica.api.runner.facade.AccountFacade;
import applica.api.runner.viewmodels.UIUserWithToken;
import applica.api.services.AccountService;
import applica.api.services.AuthService;
import applica.api.services.UserService;
import applica.api.services.exceptions.BadCredentialsException;
import applica.api.services.exceptions.MailNotFoundException;
import applica.api.services.exceptions.UserLoginMaxAttemptsException;
import applica.api.services.exceptions.UserNotFoundException;
import applica.api.services.responses.ResponseCode;
import applica.framework.library.base64.URLData;
import applica.framework.library.responses.Response;
import applica.framework.library.responses.ValueResponse;
import applica.framework.library.validation.ValidationException;
import applica.framework.security.Security;
import applica.framework.security.SecurityUtils;
import applica.framework.security.User;
import applica.framework.security.authorization.AuthorizationException;
import applica.framework.security.token.TokenGenerationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static applica.api.domain.model.UserAttempt.WAITING_TIME_IN_SECONDS;
import static applica.api.services.responses.ResponseCode.ERROR_MAIL_NOT_FOUND;
import static applica.api.services.responses.ResponseCode.ERROR_NOT_FOUND;
import static applica.framework.library.responses.Response.ERROR;
import static applica.framework.library.responses.Response.OK;

/**
 * Applica (www.applicadoit.com)
 * User: bimbobruno
 * Date: 4/17/13
 * Time: 5:47 PM
 */
@RestController
public class AccountController {


    private static final String BASE = "/account";

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountFacade accountFacade;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;



    @PostMapping(BASE + "/confirm")
    public Response confirm(String activationCode) {
        try {
            accountService.confirm(activationCode);
            return new Response(OK);
        } catch (MailNotFoundException e) {
            return new Response(ERROR_MAIL_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Response.ERROR, CustomErrorUtils.getInstance().getMessage("generic.error"));
        }
    }

    @PostMapping(BASE + "/recover")
    public Response recover(String mail) {
        try {
            accountService.recover(mail);
            return new Response(OK);
        } catch (MailNotFoundException e) {
            return new Response(ERROR_MAIL_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Response.ERROR, CustomErrorUtils.getInstance().getMessage("generic.error"));
        }
    }

//    @GetMapping(BASE + "/{userId}/cover")
//    public Response cover(@PathVariable String userId) {
//        try {
//            URLData coverImage = accountService.getCoverImage(userId, "268x129");
//            if (coverImage != null) {
//                return new ValueResponse(coverImage.write());
//            } else {
//                return new Response(ERROR_NOT_FOUND);
//            }
//        } catch (UserNotFoundException e) {
//            return new Response(ResponseCode.ERROR_USER_NOT_FOUND);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new Response(Response.ERROR, CustomErrorUtils.getInstance().getMessage("generic.error"));
//        }
//    }

    @GetMapping(BASE + "/{userId}/profile/image")
    public Response image(@PathVariable String userId) {
        try {
            URLData profileImage = accountService.getProfileImage(userId, "47x47");
            if (profileImage != null) {
                return new ValueResponse(profileImage.write());
            } else {
                return new Response(ERROR_NOT_FOUND);
            }
        } catch (UserNotFoundException e) {
            return new Response(ResponseCode.ERROR_USER_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Response.ERROR, CustomErrorUtils.getInstance().getMessage("generic.error"));
        }
    }

    private static final String CHANGE_PASSWORD_METHOD = "/changePassword";
    public static final String CHANGE_PASSWORD_URL = BASE + CHANGE_PASSWORD_METHOD;

    @RequestMapping(CHANGE_PASSWORD_URL)
    public @ResponseBody
    Response resetPassword(String currentPassword, String password, String passwordConfirm) {

        try {
            accountService.changePassword((applica.api.domain.model.auth.User) Security.withMe().getLoggedUser(), StringUtils.hasLength(currentPassword) ? SecurityUtils.encryptAndGetPassword(currentPassword) : null, password, passwordConfirm);
        } catch (ValidationException e) {
            e.getValidationResult().getErrors();
            return new Response(Response.ERROR, CustomErrorUtils.getInstance().getAllErrorMessages(e.getValidationResult().getErrors()));
        }
        User user = Security.withMe().getLoggedUser();
        String token = null;
        try {
            token = authService.token(((applica.api.domain.model.auth.User) user).getMail(), password);
        } catch (UserLoginMaxAttemptsException | BadCredentialsException | TokenGenerationException e) {
            e.printStackTrace();
        }
        return new ValueResponse(new UIUserWithToken(user, token));

    }



    @PostMapping(BASE + "/resetUserPassword")
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
    @PostMapping(BASE + "/resetPassword")
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
        }   catch (Exception e) {
            userService.updatePasswordChangeFailAttempts(attempt);
            return new Response(Response.ERROR, CustomErrorUtils.getInstance().getMessage("generic.error"));
        }
        return new Response(Response.OK);
    }

    @PostMapping(BASE + "/sendConfirmationCode")
    public Response sendConfirmationCode(String mail) {
        try {
            accountService.sendConfirmationCode(mail);
            return new Response(Response.OK);
        } catch (Exception e) {
            return new Response(Response.ERROR, CustomErrorUtils.getInstance().getMessage("generic.error"));
        }

    }

    @PostMapping(BASE + "/validateRecoveryCode")
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
