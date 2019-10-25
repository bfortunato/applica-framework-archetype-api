package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.domain.model.dossiers.Document;
import applica.api.domain.model.dossiers.DocumentType;
import applica.api.domain.model.dossiers.Dossier;
import applica.api.domain.utils.ClassUtils;
import applica.api.services.DocumentTypeService;
import applica.api.services.DocumentsService;
import applica.api.services.DossiersService;
import applica.api.services.ReportsService;
import applica.api.services.exceptions.DocumentTypeNotFoundException;
import applica.api.services.exceptions.DossierNotFoundException;
import applica.api.services.utils.FileUtils;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.fileserver.FileServer;
import applica.framework.library.options.OptionsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static applica.api.services.utils.FileUtils.TEMP_DIR_PATH;
import static applica.api.services.utils.FileUtils.generatePrefix;

@Service
public class DocumentsServiceImpl implements DocumentsService {

    @Autowired
    private OptionsManager optionsManager;

    @Autowired
    private ReportsService reportsService;

    @Autowired
    private FileServer fileServer;

    @Autowired
    private DocumentTypeService documentTypeService;

    @Autowired
    private DocumentsService documentsService;

    @Autowired
    private DossiersService dossiersService;

    @Override
    public List<DocumentType> findAllDocumentTypes() {
        return Repo.of(DocumentType.class).find(Query.build().eq(Filters.ACTIVE, true)).getRows();
    }

    @Override
    public void materializeDocumentTypes(List<Document> documents) {
        for (Document d: documents
             ) {
            materializeDocumentType(d);
        }
    }

    @Override
    public void materializeDocumentType(Document document) {
        document.setDocumentType(Repo.of(DocumentType.class).get(document.getDocumentTypeId()).orElse(null));
    }

    @Override
    public String generateFromTemplate(DocumentType documentType, Dossier dossier) throws Exception {

        dossiersService.materializeCustomer(dossier);
        dossiersService.materializeFabricator(dossier);

        HashMap<String, Object> fields = new HashMap<>();

        HashMap<Object, Object> dossierValues = ClassUtils.getAllValuesInClass(dossier);
        HashMap<Object, Object> customerValues = ClassUtils.getAllValuesInClass(dossier.getCustomer());
        HashMap<Object, Object> fabricatorValues = ClassUtils.getAllValuesInClass(dossier.getFabricator());

        for (Object o: dossierValues.keySet()
             ) {
            fields.put("dossier_"+o, dossierValues.get(o));
        }

        for (Object o: customerValues.keySet()
                ) {
            fields.put("customer_"+o, customerValues.get(o));
        }

        for (Object o: fabricatorValues.keySet()
                ) {
            fields.put("fabricator_"+o, fabricatorValues.get(o));
        }

        String fileserverPath = optionsManager.get("applica.framework.fileserver.basePath") + "\\";
        String outputPath = String.format("%s%s%s_%s", fileserverPath, TEMP_DIR_PATH, generatePrefix(), documentType.getDescription());
        InputStream inputStream = reportsService.createReport(outputPath,
                fields,
                null,
                "docx",
                new FileInputStream(fileserverPath + documentType.getTemplate().getPath()));

        return fileServer.saveFile(String.format("files/dossierDocuments/%s_%s", dossier.getSid(), documentType.getSid()), "docx", inputStream);
    }

    @Override
    public List<Document> generateFabricatorDocuments() {
        List<Document> documents = new ArrayList<>();
        for (DocumentType d: documentTypeService.findAllFabricatorDocumentsType()
             ) {
            documents.add(new Document(d.getId()));
        }
        return documents;
    }

    @Override
    public List<Document> generateDossierDocuments() {
        List<Document> documents = new ArrayList<>();
        for (DocumentType d: documentTypeService.findAllDossierDocumentsType()
                ) {
            documents.add(new Document(d.getId()));
        }
        return documents;
    }

    @Override
    public void downloadTemplate(String documentTypeId, String dossierId, HttpServletResponse response) throws DocumentTypeNotFoundException, DossierNotFoundException, IOException {
        DocumentType documentType = Repo.of(DocumentType.class).get(documentTypeId).orElseThrow(()-> new DocumentTypeNotFoundException(documentTypeId));
        Dossier dossier = Repo.of(Dossier.class).get(dossierId).orElseThrow(()-> new DossierNotFoundException(dossierId));
        Document document = dossier.getDocuments().stream().filter(d-> Objects.equals(d.getDocumentTypeId(), documentTypeId)).findFirst().orElse(null);
        String path = null;
        if (document != null){
            documentsService.materializeDocumentType(document);
            path = document.getDocumentType().getTemplate().getPath();
        }
        FileUtils.downloadAndRenameFile(documentType.getDescription() + ".docx", path, response);
    }
}