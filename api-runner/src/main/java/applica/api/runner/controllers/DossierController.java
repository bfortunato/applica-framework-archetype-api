package applica.api.runner.controllers;

import applica.api.domain.model.dossiers.Dossier;
import applica.api.services.DossiersService;
import applica.framework.library.i18n.LocalizationUtils;
import applica.framework.library.responses.Response;
import applica.framework.library.responses.ValueResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static applica.framework.library.responses.Response.ERROR;


@RestController
@RequestMapping("/dossiers")
public class DossierController {

    private final DossiersService dossiersService;

    @Autowired
    public DossierController(DossiersService dossiersService) {
        this.dossiersService = dossiersService;
    }

    @PostMapping("")
    public Response save(@RequestBody Dossier dossier) {
        try {
            dossiersService.saveDossier(dossier);
            return new Response(Response.OK);
        } catch (Exception e) {
            return new Response(Response.ERROR, LocalizationUtils.getInstance().getMessage("generic.error"));
        }
    }

    @GetMapping("/{dossierId}")
    public Response getById(@PathVariable String dossierId) {
        try {
            return new ValueResponse(dossiersService.getById(dossierId));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }

    @GetMapping("/find")
    public Response getByFabricatorId(String fabricatorId) {
        try {
            return new ValueResponse(dossiersService.findDossiersByFabricator(fabricatorId));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }

}
