package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.domain.model.dossiers.DocumentType;
import applica.api.services.DocumentTypeService;
import applica.framework.Query;
import applica.framework.Repo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentTypeServiceImpl implements DocumentTypeService {
    @Override
    public List<DocumentType> findAllFabricatorDocumentsType() {
        return Repo.of(DocumentType.class).find(Query.build().eq(Filters.ACTIVE, true).eq(Filters.ASSIGNATION_TYPE, DocumentType.FABRICATOR_PROFILE)).getRows();
    }

    @Override
    public List<DocumentType> findAllDossierDocumentsType() {
        return Repo.of(DocumentType.class).find(Query.build().eq(Filters.ACTIVE, true).ne(Filters.ASSIGNATION_TYPE, DocumentType.FABRICATOR_PROFILE)).getRows();
    }
}
