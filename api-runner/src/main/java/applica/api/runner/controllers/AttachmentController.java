package applica.api.runner.controllers;


import applica.api.services.utils.FileUtils;
import applica.framework.library.responses.Response;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    @GetMapping("/renameAndDownload")
    public Response renameAndDownload(HttpServletResponse response, String filename, String path) {

        try {
            FileUtils.downloadAndRenameFile(filename + "." + FilenameUtils.getExtension(path), path, response);
            return new Response(Response.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(Response.ERROR);
        }
    }
}
