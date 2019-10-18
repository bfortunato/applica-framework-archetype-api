package applica.api.services;

import applica.api.domain.model.dossiers.Document;
import applica.api.domain.model.dossiers.DocumentType;
import applica.api.domain.model.dossiers.Dossier;

import java.util.List;

public interface DocumentsService {

    List<DocumentType> findAllDocumentTypes();
    void materializeDocumentTypes(List<Document> documents);
    void materializeDocumentType(Document document);
    String generateFromTemplate(DocumentType documentType, Dossier dossier) throws Exception;
    List<Document> generateFabricatorDocuments();
    List<Document> generateDossierDocuments();
}
