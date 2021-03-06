package applica.api.runner.acl;

import applica.framework.security.Security;
import applica.framework.security.authorization.AuthorizationException;
import applica.framework.widgets.acl.CrudAuthorizationException;
import applica.framework.widgets.acl.CrudGuard;
import applica.framework.widgets.acl.CrudSecurityConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static applica.framework.security.authorization.BaseAuthorizationService.SUPERUSER_PERMISSION;

/**
 * Applica (www.applica.guru)
 * User: bimbobruno
 * Date: 03/02/14
 * Time: 15:41
 */
@Component
public class SimpleCrudGuard implements CrudGuard {

    @Override
    public void check(String crudPermission, String entity) throws CrudAuthorizationException {
        if (Security.withMe().isPermitted(SUPERUSER_PERMISSION)) { return; }


        String expression = CrudSecurityConfigurer.instance().getExpression(entity, crudPermission);
        if (expression == null){
            throw new CrudAuthorizationException("Expression for permission is null");
        }
        if(StringUtils.hasLength(expression)) {
            try {
                Security.withMe().authorize(expression);
            } catch (AuthorizationException e) {
                throw new CrudAuthorizationException(e);
            }
        }
    }
}
