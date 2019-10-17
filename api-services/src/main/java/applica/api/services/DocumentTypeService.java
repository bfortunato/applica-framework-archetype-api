package applica.api.services;

import applica.api.domain.model.dossiers.DocumentType;

import java.util.List;

public interface DocumentTypeService {
    List<DocumentType> findAllFabricatorDocumentsType();
    List<DocumentType> findAllDossierDocumentsType();
}
