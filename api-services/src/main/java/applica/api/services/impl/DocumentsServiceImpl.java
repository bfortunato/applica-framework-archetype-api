package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.api.domain.model.dossiers.DocumentType;
import applica.api.domain.model.dossiers.Dossier;
import applica.api.services.DocumentsService;
import applica.api.services.ReportsService;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.fileserver.FileServer;
import applica.framework.library.options.OptionsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

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

    @Override
    public List<DocumentType> findAllDocumentTypes() {
        return Repo.of(DocumentType.class).find(Query.build().eq(Filters.ACTIVE, true)).getRows();
    }

    @Override
    public String generateFromTemplate(DocumentType documentType, Dossier dossier) throws Exception {

        HashMap<String, Object> fields = new HashMap<>();
        fields.put("status", "ciao");

        String fileserverPath = optionsManager.get("applica.framework.fileserver.basePath") + "\\";
        String outputPath = String.format("%s%s%s_%s", fileserverPath, TEMP_DIR_PATH, generatePrefix(), documentType.getDescription());
        InputStream inputStream = reportsService.createReport(outputPath,
                fields,
                null,
                "docx",
                new FileInputStream(fileserverPath + documentType.getTemplate().getPath()));

        return fileServer.saveFile(String.format("files/dossierDocuments/%s_%s", dossier.getSid(), documentType.getSid()), "docx", inputStream);
    }
}
