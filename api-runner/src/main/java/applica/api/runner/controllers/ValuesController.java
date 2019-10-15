package applica.api.runner.controllers;

import applica.api.domain.data.RolesRepository;
import applica.api.domain.data.UsersRepository;
import applica.api.domain.model.Filters;
import applica.api.domain.model.auth.Role;
import applica.api.domain.model.auth.User;
import applica.api.domain.model.geo.GeoCity;
import applica.api.services.GeoCityService;
import applica.framework.Disjunction;
import applica.framework.Filter;
import applica.framework.Query;
import applica.framework.Result;
import applica.framework.library.SimpleItem;
import applica.framework.library.responses.Response;
import applica.framework.library.responses.ValueResponse;
import applica.framework.library.utils.ObjectUtils;
import applica.framework.security.authorization.Permissions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Applica (www.applica.guru)
 * User: bimbobruno
 * Date: 3/3/13
 * Time: 11:11 PM
 */
@RestController
@RequestMapping("/values")
public class ValuesController {

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    private GeoCityService geoCityService;

    @RequestMapping("/roles")
    public ValueResponse roles(String keyword) {
        List<Role> roles = rolesRepository.find(
                Query.build()
                        .like("role", keyword)
        ).getRows();

        return new ValueResponse(SimpleItem.createList(roles, "role", "id"));
    }

    @RequestMapping("/permissions")
    public ValueResponse permissions(String keyword) {
        return new ValueResponse(
            SimpleItem.createList(Permissions.instance().allPermissions(), (p) -> (String) p, (p) -> (String) p)
                .stream()
                .filter(l -> StringUtils.isEmpty(keyword) || (l.getLabel() != null && l.getLabel().toLowerCase().contains(keyword)))
                .collect(Collectors.toList())
        );
    }

    @Autowired
    private UsersRepository usersRepository;

    @RequestMapping("/users")
    public List<User> users() {
        return usersRepository.find(null).getRows();
    }

    @GetMapping("/cities")
    public Response getCities(HttpServletRequest request) {

        Query query = ObjectUtils.bind(new Query(), new ServletRequestParameterPropertyValues(request));

        Result<GeoCity> geoCityResult = null;
        Disjunction disjunction = new Disjunction();
        try {
            if(query.getKeyword() != null) {
                disjunction.getChildren().add(new Filter(Filters.DESCRIPTION, query.getKeyword(), Filter.LIKE));
                disjunction.getChildren().add(new Filter(Filters.CAP, query.getKeyword(), Filter.LIKE));
                query.getFilters().add(disjunction);
            }
            else query.getFilters().add(new Filter(Filters.DESCRIPTION, query.getKeyword(), Filter.LIKE));

            geoCityResult = geoCityService.filterByQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Response.ERROR, e.getMessage());
        }

        return new ValueResponse(geoCityResult);
    }

}
