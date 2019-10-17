package applica.api.runner.controllers;

import applica.api.domain.model.dossiers.Dossier;
import applica.api.domain.model.dossiers.PriceCalculatorSheet;
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

    @GetMapping("")
    public Response getByFabricatorId(String fabricatorId) {
        try {
            return new ValueResponse(dossiersService.findDossiersByFabricator(fabricatorId));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }

    @PostMapping("/{dossierId}/attach/{documentTypeId}")
    public Response attach(@PathVariable String dossierId, @PathVariable String documentTypeId, @RequestBody byte[] attachmentData, @RequestBody String attachmentName) {
        try {
            dossiersService.attachDocument(dossierId, documentTypeId, attachmentData, attachmentName);
            return new Response(Response.OK);
        } catch (Exception e) {
            return new Response(Response.ERROR, LocalizationUtils.getInstance().getMessage("generic.error"));
        }
    }

    @PostMapping("/{dossierId}/clear/{documentTypeId}")
    public Response save(@PathVariable String dossierId, @PathVariable String documentTypeId) {
        try {
            dossiersService.clearDocumentAttachment(dossierId, documentTypeId);
            return new Response(Response.OK);
        } catch (Exception e) {
            return new Response(Response.ERROR, LocalizationUtils.getInstance().getMessage("generic.error"));
        }
    }

    @PostMapping("/{dossierId}/refuse/{documentTypeId}")
    public Response refuse(@PathVariable String dossierId, @PathVariable String documentTypeId) {
        try {
            dossiersService.refuseDocument(dossierId, documentTypeId);
            return new Response(Response.OK);
        } catch (Exception e) {
            return new Response(Response.ERROR, LocalizationUtils.getInstance().getMessage("generic.error"));
        }
    }

    @PostMapping("/{dossierId}/confirmQuotation}")
    public Response confirmQuotation(@PathVariable String dossierId) {
        try {
            dossiersService.confirmQuotation(dossierId);
            return new Response(Response.OK);
        } catch (Exception e) {
            return new Response(Response.ERROR, LocalizationUtils.getInstance().getMessage("generic.error"));
        }
    }

    @PostMapping("/{dossierId}/commit}")
    public Response commit(@PathVariable String dossierId) {
        try {
            dossiersService.commit(dossierId);
            return new Response(Response.OK);
        } catch (Exception e) {
            return new Response(Response.ERROR, LocalizationUtils.getInstance().getMessage("generic.error"));
        }
    }

    @PostMapping("/{dossierId}/candidate}")
    public Response candidate(@PathVariable String dossierId) {
        try {
            dossiersService.candidate(dossierId);
            return new Response(Response.OK);
        } catch (Exception e) {
            return new Response(Response.ERROR, LocalizationUtils.getInstance().getMessage("generic.error"));
        }
    }

    @PostMapping("/{dossierId}/approve}")
    public Response approve(@PathVariable String dossierId) {
        try {
            dossiersService.approve(dossierId);
            return new Response(Response.OK);
        } catch (Exception e) {
            return new Response(Response.ERROR, LocalizationUtils.getInstance().getMessage("generic.error"));
        }
    }

    @PostMapping("/{dossierId}/refuse}")
    public Response refuse(@PathVariable String dossierId) {
        try {
            dossiersService.refuse(dossierId);
            return new Response(Response.OK);
        } catch (Exception e) {
            return new Response(Response.ERROR, LocalizationUtils.getInstance().getMessage("generic.error"));
        }
    }

    @PostMapping("/{dossierId}/payOff}")
    public Response payOff(@PathVariable String dossierId) {
        try {
            dossiersService.payOff(dossierId);
            return new Response(Response.OK);
        } catch (Exception e) {
            return new Response(Response.ERROR, LocalizationUtils.getInstance().getMessage("generic.error"));
        }
    }

    @GetMapping("/serviceCost")
    public Response serviceCost(@RequestBody PriceCalculatorSheet priceCalculatorSheet) {
        try {
            return new ValueResponse(dossiersService.calculateServiceCost(priceCalculatorSheet));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }

    @GetMapping("/recommendedPrice")
    public Response recommendedPrice(@RequestBody PriceCalculatorSheet priceCalculatorSheet) {
        try {
            return new ValueResponse(dossiersService.calculateRecommendedPrice(priceCalculatorSheet));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }

    @GetMapping("/simulateFinancing")
    public Response simulateFinancing(@RequestBody PriceCalculatorSheet priceCalculatorSheet) {
        try {
            return new ValueResponse(dossiersService.simulateFinancing(priceCalculatorSheet));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }
}