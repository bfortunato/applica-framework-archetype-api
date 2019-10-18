package applica.api.runner.controllers;

import applica.api.domain.model.users.Customer;
import applica.api.services.CustomersService;
import applica.framework.library.i18n.LocalizationUtils;
import applica.framework.library.responses.Response;
import applica.framework.library.responses.ValueResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static applica.framework.library.responses.Response.ERROR;
import static applica.framework.library.responses.Response.OK;


@RestController
@RequestMapping("/customers")
public class CustomersController {

    private final CustomersService customersService;

    @Autowired
    public CustomersController(CustomersService customersService) {
        this.customersService = customersService;
    }

    @PostMapping("")
    public Response save(@RequestBody Customer customer) {
        try {
            customersService.saveCustomer(customer);
            return new Response(Response.OK);
        } catch (Exception e) {
            return new Response(Response.ERROR, LocalizationUtils.getInstance().getMessage("generic.error"));
        }
    }

    @GetMapping("")
    public Response getByKeyword(String keyword) {
        try {
            return new ValueResponse(customersService.findCustomerByKeyword(keyword, false));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }

    @DeleteMapping("/{customerId}")
    public Response delete(@PathVariable String customerId) {
        try {
            customersService.deleteCustomer(customerId);
            return new Response(OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }

}
