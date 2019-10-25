package applica.api.runner.controllers;

import applica.api.domain.exceptions.WorkflowException;
import applica.api.domain.model.dossiers.PriceCalculatorSheet;
import applica.api.services.DossiersService;
import applica.api.services.FabricatorService;
import applica.api.services.exceptions.*;
import applica.api.services.responses.ErrorResponse;
import applica.api.services.responses.ResponseCode;
import applica.framework.library.responses.Response;
import applica.framework.library.responses.ValueResponse;
import applica.framework.widgets.operations.OperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static applica.framework.library.responses.Response.ERROR;


@RestController
@RequestMapping("/dossiers")
public class DossierController {

    private final DossiersService dossiersService;

    private final FabricatorService fabricatorService;

    @Autowired
    public DossierController(DossiersService dossiersService, FabricatorService fabricatorService) {
        this.dossiersService = dossiersService;
        this.fabricatorService = fabricatorService;
    }

    @PostMapping("{dossierId}/edit")
    public Response save(@PathVariable String dossierId, String customerId, String fabricatorId, double significantValue, double nonSignificantValue, double serviceValue, String notes) {
        try {
            return new ValueResponse(dossiersService.edit(dossierId, fabricatorId, customerId, new PriceCalculatorSheet(significantValue, nonSignificantValue, serviceValue), notes));
        } catch (FabricatorNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_FABRICATOR_NOT_FOUND, e.getFabricatorId());
        } catch (CustomerNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_CUSTOMER_NOT_FOUND, e.getCustomerId());
        } catch (DossierNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_DOSSIER_NOT_FOUND, e.getDossierId());
        }
    }

    @PostMapping("/quotation")
    public Response create(String customerId, String fabricatorId, double significantValue, double nonSignificantValue, double serviceValue, String notes) {
        try {
            return new ValueResponse(dossiersService.create(StringUtils.hasLength(fabricatorId) ? fabricatorId : fabricatorService.getLoggedUserFabricatorId(), customerId, new PriceCalculatorSheet(significantValue, nonSignificantValue, serviceValue), notes));
        } catch (OperationException e) {
            return new ErrorResponse(e.getErrorCode(), e.getData());
        } catch (WorkflowException e) {
            return new ErrorResponse(ResponseCode.ERROR_INVALID_DATA, null);
        } catch (CustomerNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_CUSTOMER_NOT_FOUND, e.getCustomerId());
        } catch (FabricatorNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_FABRICATOR_NOT_FOUND, e.getFabricatorId());
        }

    }

    @GetMapping("/{dossierId}")
    public Response getById(@PathVariable String dossierId) {
        try {
            return new ValueResponse(dossiersService.getById(dossierId));
        } catch (DossierNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_DOSSIER_NOT_FOUND, e.getDossierId());
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
        } catch (DossierNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_DOSSIER_NOT_FOUND, e.getDossierId());
        }
    }

    @PostMapping("/{dossierId}/attachPath/{documentTypeId}")
    public Response attach(@PathVariable String dossierId, @PathVariable String documentTypeId, String path) {
        try {
            return new ValueResponse(dossiersService.attachDocument(dossierId, documentTypeId, path));
        } catch (DossierNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_DOSSIER_NOT_FOUND, e.getDossierId());
        } catch (IOException e) {
            e.printStackTrace();
            return new ErrorResponse(ResponseCode.ERROR_INVALID_DATA, null);
        }
    }

    @PostMapping("/{dossierId}/clear/{documentTypeId}")
    public Response clear(@PathVariable String dossierId, @PathVariable String documentTypeId) {
        try {
            return new ValueResponse(dossiersService.clearDocumentAttachment(dossierId, documentTypeId));
        } catch (DossierNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_DOSSIER_NOT_FOUND, e.getDossierId());
        } catch (DocumentNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_DOCUMENT_NOT_FOUND, e.getDocumentId());
        }
    }

    @PostMapping("/{dossierId}/refuse/{documentTypeId}")
    public Response refuse(@PathVariable String dossierId, @PathVariable String documentTypeId, String refuseReason) {
        try {
            return new ValueResponse(dossiersService.refuseDocument(dossierId, documentTypeId, refuseReason));
        } catch (DossierNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_DOSSIER_NOT_FOUND, e.getDossierId());
        } catch (DocumentTypeNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_DOCUMENT_TYPE_NOT_FOUND, e.getDocumentTypeId());
        }
    }

    @PostMapping("/{dossierId}/quotation")
    public Response quotation(@PathVariable String dossierId) {
        try {
            return new ValueResponse(dossiersService.quotation(dossierId));
        } catch (WorkflowException e) {
            return new ErrorResponse(ResponseCode.ERROR_INVALID_DATA, null);
        } catch (DossierNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_DOSSIER_NOT_FOUND, e.getDossierId());
        }
    }

    @PostMapping("/{dossierId}/confirmQuotation")
    public Response confirmQuotation(@PathVariable String dossierId) {
        try {
            return new ValueResponse(dossiersService.confirmQuotation(dossierId));
        } catch (WorkflowException e) {
            return new ErrorResponse(ResponseCode.ERROR_INVALID_DATA, null);
        } catch (DossierNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_DOSSIER_NOT_FOUND, e.getDossierId());
        }
    }

    @PostMapping("/{dossierId}/commit")
    public Response commit(@PathVariable String dossierId) {
        try {
            return new ValueResponse(dossiersService.commit(dossierId));
        } catch (WorkflowException e) {
            return new ErrorResponse(ResponseCode.ERROR_WORKFLOW, null);
        } catch (DossierNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_DOSSIER_NOT_FOUND, e.getDossierId());
        }
    }

    @PostMapping("/{dossierId}/candidate")
    public Response candidate(@PathVariable String dossierId) {
        try {
            return new ValueResponse(dossiersService.candidate(dossierId));
        } catch (WorkflowException e) {
            return new ErrorResponse(ResponseCode.ERROR_WORKFLOW, null);
        } catch (DossierNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_DOSSIER_NOT_FOUND, e.getDossierId());
        }

    }

    @PostMapping("/{dossierId}/approve")
    public Response approve(@PathVariable String dossierId) {
        try {
            return new ValueResponse(dossiersService.approve(dossierId));
        } catch (WorkflowException e) {
            return new ErrorResponse(ResponseCode.ERROR_WORKFLOW, null);
        } catch (DossierNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_DOSSIER_NOT_FOUND, e.getDossierId());
        }

    }

    @PostMapping("/{dossierId}/refuse")
    public Response refuse(@PathVariable String dossierId) {
        try {
            return new ValueResponse(dossiersService.refuse(dossierId));
        } catch (WorkflowException e) {
            return new ErrorResponse(ResponseCode.ERROR_WORKFLOW, null);
        } catch (DossierNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_DOSSIER_NOT_FOUND, e.getDossierId());
        }
    }

    @PostMapping("/{dossierId}/payOff")
    public Response payOff(@PathVariable String dossierId) {
        try {
            return new ValueResponse(dossiersService.payOff(dossierId));
        } catch (WorkflowException e) {
            return new ErrorResponse(ResponseCode.ERROR_WORKFLOW, null);
        } catch (DossierNotFoundException e) {
            return new ErrorResponse(ResponseCode.ERROR_DOSSIER_NOT_FOUND, e.getDossierId());
        }
    }

    @GetMapping("/calculator/serviceCost")
    public Response serviceCost(double significantValue, double nonSignificantValue, double serviceValue) {
        try {
            return new ValueResponse(dossiersService.calculateServiceCost(new PriceCalculatorSheet(significantValue, nonSignificantValue, serviceValue)));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }

    @GetMapping("/calculator/recommendedPrice")
    public Response recommendedPrice(double significantValue, double nonSignificantValue, double serviceValue) {
        try {
            return new ValueResponse(dossiersService.calculateRecommendedPrice(new PriceCalculatorSheet(significantValue, nonSignificantValue, serviceValue)));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }

    @GetMapping("/calculator/simulateFinancing")
    public Response simulateFinancing(double significantValue, double nonSignificantValue, double serviceValue) {
        try {
            return new ValueResponse(dossiersService.simulateFinancing(new PriceCalculatorSheet(significantValue, nonSignificantValue, serviceValue)));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }

    @GetMapping("/count")
    public Response count() {
        try {
            return new ValueResponse(dossiersService.count());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }
}
