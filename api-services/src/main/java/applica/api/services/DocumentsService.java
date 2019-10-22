package applica.api.services;

import applica.api.domain.model.dossiers.Document;
import applica.api.domain.model.dossiers.DocumentType;
import applica.api.domain.model.dossiers.Dossier;
import applica.api.services.exceptions.DocumentTypeNotFoundException;
import applica.api.services.exceptions.DossierNotFoundException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface DocumentsService {

    List<DocumentType> findAllDocumentTypes();
    void materializeDocumentTypes(List<Document> documents);
    void materializeDocumentType(Document document);
    String generateFromTemplate(DocumentType documentType, Dossier dossier) throws Exception;
    List<Document> generateFabricatorDocuments();
    List<Document> generateDossierDocuments();

    void downloadTemplate(String documentTypeId, String dossierId, HttpServletResponse response) throws DocumentTypeNotFoundException, DossierNotFoundException, IOException;
}
