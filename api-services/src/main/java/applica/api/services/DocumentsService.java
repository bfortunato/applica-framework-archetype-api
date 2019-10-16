package applica.api.services;

import applica.api.domain.model.dossiers.DocumentType;
import applica.api.domain.model.dossiers.Dossier;

import java.util.List;

public interface DocumentsService {

    List<DocumentType> findAllDocumentTypes();
    String generateFromTemplate(DocumentType documentType, Dossier dossier) throws Exception;

}
