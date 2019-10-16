package applica.api.runner.facade;

import applica.api.domain.model.NewAttachmentData;
import applica.api.domain.utils.CustomLocalizationUtils;
import applica.api.services.exceptions.FileNotSupportedException;
import applica.framework.Entity;
import applica.framework.fileserver.FileServer;
import applica.framework.fileserver.facade.EmptyFileException;
import applica.framework.fileserver.viewmodel.UIImageUpload;
import applica.framework.widgets.mapping.Attachment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

import static applica.api.services.utils.FileUtils.TEMP_DIR;
import static applica.api.services.utils.FileUtils.canBeUploadedInBackend;

/**
 * Applica (www.applica.guru)
 * User: bimbobruno
 * Date: 07/03/14
 * Time: 13:06
 */
@Component
public class CustomUploadFacade {


    @Autowired
    private FileServer fileServer;

    /**
     * Uploads a generic image to images server
     * @param data
     * @return the path of inserted image
     */
    public String uploadImage(UIImageUpload data) throws EmptyFileException, IOException {
        Assert.notNull(data, "Data cannot be null");

        if(data.getImage() == null || data.getImage().isEmpty()) {
            throw new EmptyFileException();
        }

        String extension = FilenameUtils.getExtension(data.getImage().getOriginalFilename());

        if(data.getPath().startsWith("/")) {
            data.setPath(data.getPath().substring(1));
        }
        String path = "";

        path = fileServer.saveImage("images/".concat(data.getPath()), extension, data.getImage().getInputStream());
        return path;
    }

    public String uploadFile(MultipartFile data) throws IOException, FileNotSupportedException {
        Assert.notNull(data, "Data cannot be null");

        String extension = FilenameUtils.getExtension(data.getOriginalFilename());



        String path = String.format("files/%s/", TEMP_DIR);

        if (canBeUploadedInBackend(data.getOriginalFilename()))
            return fileServer.saveFile(path, extension, data.getInputStream());

        throw new FileNotSupportedException(CustomLocalizationUtils.getInstance().getMessage("file.not.supported"));

    }

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
            if (StringUtils.isNotEmpty(file.getPath())) {
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
                if (fileData.getPath().contains(TEMP_DIR)) {
                    filePath = fileServer.copyFile(fileData.getPath(), path);
                }

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

    public String uploadFile(byte[] file, String filename) throws IOException {
        String extension = FilenameUtils.getExtension(filename);

        String path = String.format("files/%s/", TEMP_DIR).concat(String.format("%s.%s", RandomStringUtils.random(10, true, true), extension));

        return fileServer.saveFile(path,extension, new ByteArrayInputStream(file));
    }
}
