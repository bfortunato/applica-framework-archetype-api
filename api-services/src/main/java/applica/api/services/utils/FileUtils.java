package applica.api.services.utils;

import applica.framework.ApplicationContextProvider;
import applica.framework.fileserver.FileServer;
import applica.framework.fileserver.MimeUtils;
import applica.framework.library.options.OptionsManager;
import applica.framework.widgets.mapping.Attachment;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.bson.internal.Base64;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class FileUtils {

    public static final String IMAGE = "image";
    public static final String AUDIO = "audio";
    public static final String VIDEO = "video";
    public static final String FILE = "file";

    //Formati file accettati dal file uploader del backend
    public static final List<String> ACCEPTED_BACKEND_EXTENSIONS = Arrays.asList("apk", "csv", "pdf", "doc", "docx");


    public static final List<String> ACCEPTED_EXTENSION = Arrays.asList("jpg", "jpeg", "png", "tiff", "bmp", "gif", "mp4", "mp3", "aac", "wav", "avi", "mov", "mpeg", "mpg", "pdf", "xls", "xlsx", "doc", "docx", "wmv", "ppt", "pptx", "txt", "csv",  "rtf", "zip", "rar", "7z", "sdoc");

    //Siamo passati da un sistema a blacklist ad uno a whitelist che accetta ACCEPTED_EXTENSION
    @Deprecated
    public static final List<String> NOT_ACCEPTED_BACKEND_EXTENSIONS = Arrays.asList("html", "js", "jsx", "xhtml", "css", "php", "htm", "xml");
    public static final String TEMP_DIR = "_temp";
    public static final String TEMP_DIR_PATH = String.format("files%s%s%s", File.separator, TEMP_DIR, File.separator);

    public static String getHumanReadableSize(long size) {
        return org.apache.commons.io.FileUtils.byteCountToDisplaySize(size);
    }

    public static InputStream getResourceFileInputStream(String resourcePath) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(resourcePath);
        return classPathResource.getInputStream();
    }

    public static void convertToPDF(InputStream doc, String pdfPath) {
        File file = new File(pdfPath);
        convertToPDF(doc, file);
    }

    public static void convertToPDF(InputStream doc, File pdfPath) {
//        try {
//            XWPFDocument document = new XWPFDocument(doc);
//            PdfOptions options = PdfOptions.create();
//            OutputStream out = new FileOutputStream(pdfPath);
//            PdfConverter.getInstance().convert(document, out, options);
//            System.out.println("Done");
//        } catch (FileNotFoundException ex) {
//            System.out.println(ex.getMessage());
//        } catch (IOException ex) {
//
//            System.out.println(ex.getMessage());
//        }
    }

    public static String getMimeType(String path) {
        Tika tika = new Tika();
        return tika.detect(path);
    }

    public static String getFileType(String path) {
        Tika tika = new Tika();
        String mimeType = tika.detect(path);
        if (mimeType.startsWith("audio/"))
            return AUDIO;
        else if (mimeType.startsWith("image/"))
            return IMAGE;
        else if (mimeType.startsWith("video/"))
            return VIDEO;
        return FILE;
    }

    public static String getFileType(InputStream inputStream, String fileName) {
        Tika tika = new Tika();
        try {
            String mimeType = tika.detect(inputStream, fileName);
            if (mimeType.startsWith("audio/"))
                return AUDIO;
            else if (mimeType.startsWith("image/"))
                return IMAGE;
            else if (mimeType.startsWith("video/"))
                return VIDEO;
            return FILE;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static Attachment generateAttachmentFromByteArray(byte[] attachmentByte, String filename, String fileServerPath) throws IOException {
        Attachment attachment = null;

        if (attachmentByte != null && attachmentByte.length > 0 && StringUtils.hasLength(filename) && StringUtils.hasLength(fileServerPath)) {
            InputStream stream = new ByteArrayInputStream(attachmentByte);
            FileServer fileServer = ApplicationContextProvider.provide().getBean(FileServer.class);


            String file = fileServer.saveFile(fileServerPath, FilenameUtils.getExtension(filename), stream);
            long size = attachmentByte.length;
            attachment = new Attachment();
            attachment.setName(filename);
            attachment.setSize(size);
            attachment.setPath(file);
        }

        return attachment;
    }

    public static Attachment generateAttachmentFromBase64(String imageData, String filename, String fileServerPath) throws IOException {
        Attachment attachment = null;

        if (StringUtils.hasLength(imageData) && StringUtils.hasLength(filename) && StringUtils.hasLength(fileServerPath)) {
            byte[] data = Base64.decode(imageData);
            InputStream stream = new ByteArrayInputStream(data);
            FileServer fileServer = ApplicationContextProvider.provide().getBean(FileServer.class);


            String file = fileServer.saveFile(fileServerPath, FilenameUtils.getExtension(filename), stream);
            long size = fileServer.getFileSize(file);
            attachment = new Attachment();
            attachment.setName(filename);
            attachment.setSize((int) size);
            attachment.setPath(file);
        }

        return attachment;
    }


    public static void deleteFromFileserver(String previousImage) {
        if (StringUtils.hasLength(previousImage)) {
            FileServer fileServer = ApplicationContextProvider.provide().getBean(FileServer.class);
            try {
                fileServer.deleteFile(previousImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void downloadAndRenameFile(String filename, String filePath, HttpServletResponse response) throws IOException {
        FileServer fileServer = ApplicationContextProvider.provide().getBean(FileServer.class);
        InputStream fileInputStream = fileServer.getFile(filePath);
        filename = filename != null? filename : FilenameUtils.getName(filePath);
        downloadFile(fileInputStream, filename, response);
    }

    public static void downloadAndRenameFile(String filename, InputStream fileInputStream, HttpServletResponse response) throws IOException {
        downloadFile(fileInputStream, filename, response);
    }

    public static void downloadFile(InputStream fileInputStream, String fileName, HttpServletResponse response) throws IOException {
        response.setContentType(MimeUtils.getMimeType(FilenameUtils.getExtension(fileName)));
        response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", fileName.replaceAll("\\s+", "_")));
        response.setStatus(200);
        IOUtils.copy(fileInputStream, response.getOutputStream());
        IOUtils.closeQuietly(fileInputStream);
    }

    public static String getAttachmentFullUrl(String path) {
        if (path == null)
            return null;
        OptionsManager optionsManager = ApplicationContextProvider.provide().getBean(OptionsManager.class);
        return String.format("%s?path=%s", optionsManager.get("fileserver.base"), path );
    }

    public static String generateTempPath(String report) {
        OptionsManager optionsManager = ApplicationContextProvider.provide().getBean(OptionsManager.class);
        return generateTempPath(optionsManager, report);
        }

    private static String generateTempPath(OptionsManager optionsManager, String report) {
        String fileSeparator = File.separator;
        String[] s = report.split(fileSeparator);
        report = s.length > 1 ? s[s.length -1 ] : report;
        return String.format("%s%s%s_%s", optionsManager.get("applica.framework.fileserver.basePath"),TEMP_DIR_PATH, generatePrefix(), report);
    }

    public static String generatePrefix() {
        return String.valueOf(new Date().getTime());
    }

    public static String moveToTempPath(OptionsManager optionsManager, InputStream inputStream, String filenameWithExtention) throws IOException {
       String path = generateTempPath(optionsManager, filenameWithExtention);
        // 3) Generate report by merging Java model with the ODT
        File file = new File(path);
        OutputStream out = new FileOutputStream(file);
        IOUtils.copy(inputStream,out);
        inputStream.close();
        out.close();
        return path;
    }

    public static boolean canBeUploadedInBackend(String path) {
        return ACCEPTED_BACKEND_EXTENSIONS.contains(FilenameUtils.getExtension(path)) || getFileType(path).equals(IMAGE);
    }

    public static boolean canBeUploaded(String path) {
        return ACCEPTED_EXTENSION.contains(FilenameUtils.getExtension(path).toLowerCase());
    }

}
