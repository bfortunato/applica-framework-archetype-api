package applica.api.services;

import applica.api.domain.model.dossiers.DocumentType;

import java.io.OutputStreamWriter;
import java.util.List;

public interface DocumentsService {

    List<DocumentType> findAllDocumentTypes();
    void generateFromTemplate(OutputStreamWriter writer, Object documentTypeId, Object dossierId);

}
