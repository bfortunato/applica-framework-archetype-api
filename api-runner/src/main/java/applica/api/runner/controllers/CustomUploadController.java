package applica.api.runner.controllers;

import applica.api.domain.utils.CustomLocalizationUtils;
import applica.api.runner.facade.CustomUploadFacade;
import applica.api.runner.viewmodels.UIAttachment;
import applica.api.runner.viewmodels.UIFileUploadResponse;
import applica.framework.library.responses.Response;
import applica.framework.library.responses.ValueResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static applica.framework.library.responses.Response.ERROR;

/**
 * Applica (www.applica.guru)
 * User: bimbobruno
 * Date: 07/03/14
 * Time: 15:01
 */
@RestController
public class CustomUploadController {
    public static final String BASE = "/upload";

    @Autowired
    private CustomUploadFacade uploadFacade;

    @PostMapping(BASE + "/file")
    public @ResponseBody
    Response uploadFile(@RequestParam("file") MultipartFile file) {

        String path;
        try {
            path = uploadFacade.uploadFile(file);
          //  String originalFileName = data.getFile().getName();
            return new ValueResponse(new UIFileUploadResponse(file.getOriginalFilename(), path));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ERROR, CustomLocalizationUtils.getInstance().getMessage("generic.error"));
        }

    }

    @PostMapping(BASE + "/fileByte")
    public @ResponseBody
    Response uploadFile(@RequestBody String fileToString) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        try {
            UIAttachment attachment = mapper.readValue(mapper.readValue(fileToString, String.class), UIAttachment.class);
            return new ValueResponse(uploadFacade.uploadFile(attachment.getBytes(), attachment.getFilename()));
        } catch (Exception e) {
            return new Response(ERROR, CustomLocalizationUtils.getInstance().getMessage("generic.error"));
        }
    }
}