package applica.api.runner.controllers;

import applica.api.domain.utils.CustomErrorUtils;
import applica.api.domain.utils.CustomLocalizationUtils;
import applica.api.services.AccountService;
import applica.api.services.AuthService;
import applica.api.services.exceptions.BadCredentialsException;
import applica.api.services.exceptions.UserLoginMaxAttemptsException;
import applica.api.services.responses.ErrorResponse;
import applica.api.services.responses.LoginResponse;
import applica.framework.library.i18n.controllers.LocalizedController;
import applica.framework.library.responses.Response;
import applica.framework.security.Security;
import applica.framework.security.User;
import applica.framework.security.token.TokenFormatException;
import applica.framework.security.token.TokenGenerationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static applica.api.services.responses.ResponseCode.*;
import static applica.framework.library.responses.Response.ERROR;

/**
 * Applica (www.applica.guru)
 * User: bimbobruno
 * Date: 3/19/2016
 * Time: 4:22 PM
 */
@RestController
@RequestMapping("/auth")
public class AuthController extends LocalizedController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/login")
    public Response login(String mail, String password) {
        try {
            String token = authService.token(mail, password);
            User user = Security.withMe().getLoggedUser();
            return new LoginResponse(token, user);
        } catch (BadCredentialsException e) {
            return new Response(ERROR_BAD_CREDENTIALS, CustomLocalizationUtils.getInstance().getMessage("error.bad.credentials"));
        } catch (UserLoginMaxAttemptsException e) {
            return new ErrorResponse(ERROR, e.getMessage());
        }  catch (Exception e) {
            return new Response(Response.ERROR, CustomErrorUtils.getInstance().getMessage("error.generic"));
        }
    }

    @GetMapping("/freshToken")
    public Response freshToken(HttpServletRequest request) {
        try {
            String currentToken = request.getHeader("TOKEN");
            String token = authService.freshToken(currentToken);
            return new LoginResponse(token);
        } catch (TokenGenerationException e) {
            return new Response(ERROR_TOKEN_GENERATION);
        } catch (TokenFormatException e) {
            return new Response(ERROR_TOKEN_FORMAT);
        } catch (BadCredentialsException e) {
            return new Response(ERROR_BAD_CREDENTIALS);
        } catch (UserLoginMaxAttemptsException e) {
            return new Response(ERROR, e.getMessage());
        }  catch (Exception e) {
            return new Response(Response.ERROR, CustomErrorUtils.getInstance().getMessage("error.generic"));
        }
    }

}
