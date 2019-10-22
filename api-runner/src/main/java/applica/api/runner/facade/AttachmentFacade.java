package applica.api.runner.facade;

import applica.api.domain.model.NewAttachmentData;
import applica.api.domain.model.dossiers.DocumentType;
import applica.api.domain.model.dossiers.Dossier;
import applica.api.services.DocumentsService;
import applica.api.services.exceptions.DocumentTypeNotFoundException;
import applica.api.services.exceptions.DossierNotFoundException;
import applica.api.services.utils.FileUtils;
import applica.framework.Entity;
import applica.framework.Repo;
import applica.framework.fileserver.FileServer;
import applica.framework.widgets.mapping.Attachment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class AttachmentFacade {

    @Autowired
    private FileServer fileServer;

    @Autowired
    private DocumentsService documentsService;

    public void attachmentToDataUrl(Entity source, ObjectNode destination, String sourceProperty, String destinationProperty) {
        Objects.requireNonNull(fileServer, "Fileserver not injected");
        Objects.requireNonNull(source, "Cannot convert entity to image: entity is null");
        Objects.requireNonNull(destination, "Cannot convert entity to image: node is null");

        Attachment file = null;
        try {
            file = (Attachment) PropertyUtils.getProperty(source, sourceProperty);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (file != null) {
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(file.getPath())) {
                destination.putPOJO(destinationProperty, new NewAttachmentData(file.getName(), file.getSize(), file.getPath()));
            }
        }
    }

    public void dataUrlToAttachment(ObjectNode source, Entity destination, String sourceProperty, String destinationProperty, String path) {
        Objects.requireNonNull(fileServer, "Fileserver not injected");
        Objects.requireNonNull(destination, "Cannot convert entity to image: entity is null");
        Objects.requireNonNull(source, "Cannot convert entity to image: node is null");

        NewAttachmentData fileData = null;
        Attachment fileUrls = null;
        Attachment actualFiles;
        try {
            actualFiles = (Attachment) PropertyUtils.getProperty(destination, destinationProperty);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (source.get(sourceProperty) != null && !source.get(sourceProperty).isNull()) {
            JsonNode n = source.get(sourceProperty);
            fileData = new NewAttachmentData(n.get("name").asText(), n.get("size").asLong(), n.get("path").asText());
        }

        if (fileData != null) {
            try {
                String filePath = null;
                if (fileData.getPath().contains("temp")) {
                    filePath = fileServer.copyFile(fileData.getPath(), path);
                } else
                    filePath = fileData.getPath();

                fileUrls = new Attachment(fileData.getName(), filePath, fileData.getSize());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        try {
            PropertyUtils.setProperty(destination, destinationProperty, fileUrls);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (actualFiles != null && (fileUrls == null || !actualFiles.getPath().equals(fileUrls.getPath()))) {
            try {
                fileServer.deleteFile(actualFiles.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void generateAndDownloadFromTemplate(Object documentTypeId, Object dossierId, HttpServletResponse response) throws DocumentTypeNotFoundException, DossierNotFoundException, IOException {
        DocumentType documentType = Repo.of(DocumentType.class).get(documentTypeId).orElseThrow(()-> new DocumentTypeNotFoundException(documentTypeId));
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(()-> new DossierNotFoundException(dossierId));
        String path = null;
        try {
            path = documentsService.generateFromTemplate(documentType, dossier);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileUtils.downloadAndRenameFile(documentType.getDescription() + ".docx", path, response);
    }
}
