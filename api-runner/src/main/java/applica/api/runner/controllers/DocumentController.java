package applica.api.runner.controllers;

import applica.api.runner.facade.AttachmentFacade;
import applica.api.services.DocumentsService;
import applica.framework.library.responses.Response;
import applica.framework.library.responses.ValueResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import static applica.framework.library.responses.Response.ERROR;


@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentsService documentsService;
    private final AttachmentFacade attachmentFacade;

    public DocumentController(DocumentsService documentsService, AttachmentFacade attachmentFacade) {
        this.documentsService = documentsService;
        this.attachmentFacade = attachmentFacade;
    }

    @GetMapping("")
    public Response findAllDocumentTypes() {
        try {
            return new ValueResponse(documentsService.findAllDocumentTypes());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR);
        }
    }

    @GetMapping("/{dossierId}/generate")
    public void generate(HttpServletResponse response, @PathVariable String dossierId, String documentTypeId) {
        try {
            attachmentFacade.generateAndDownloadFromTemplate(documentTypeId, dossierId, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
