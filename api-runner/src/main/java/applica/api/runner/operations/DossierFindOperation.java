package applica.api.runner.operations;

import applica.api.domain.model.dossiers.DocumentType;
import applica.api.domain.model.dossiers.Dossier;
import applica.api.services.DocumentsService;
import applica.api.services.DossiersService;
import applica.framework.Entity;
import applica.framework.widgets.operations.BaseFindOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DossierFindOperation extends BaseFindOperation {

    @Autowired
    private DossiersService dossiersService;

    @Autowired
    private DocumentsService documentsService;

    @Override
    public Class<? extends Entity> getEntityType() {
        return Dossier.class;
    }



//    @Override
//    protected Result<? extends Entity> fetch(Query query) {
//        if (query.hasFilter(Filters.ACTIVE)) {
//            FilterUtils.addActiveFilter(query);
//        }
//        if (query.hasFilter(Filters.NAME)){
//            Disjunction disjunction = new Disjunction();
//            disjunction.getChildren().add(new Filter(Filters.SOCIAL_REASON, query.getFilterValue(Filters.NAME), Filter.LIKE));
//            disjunction.getChildren().add(new Filter(Filters.FIRST_NAME, query.getFilterValue(Filters.NAME), Filter.LIKE));
//            disjunction.getChildren().add(new Filter(Filters.LASTNAME, query.getFilterValue(Filters.NAME), Filter.LIKE));
//            query.getFilters().removeIf(fi -> fi.getProperty().equals(Filters.NAME));
//            query.getFilters().add(disjunction);
//        }
//        return customersService.findCustomerByQuery(query);
//    }

    @Override
    public void onSerializeEntity(ObjectNode node, Entity entity) {
        Dossier dossier = ((Dossier) entity);
        dossier = dossiersService.materializeCustomer(dossier);
        dossier = dossiersService.materializeFabricator(dossier);
        documentsService.materializeDocumentTypes(dossier.getDocuments());
        node.putPOJO("customer", dossier.getCustomer());
        node.putPOJO("fabricator", dossier.getFabricator());
        node.put("_preparatoryDocumentations", String.format("%d di %d", dossier.getDocuments().stream().filter(d->Objects.equals(d.getDocumentType().getAssignationType(), DocumentType.PREPARATORY_DOCUMENTATION) && d.getFile() != null && d.isValid()).count(), dossier.getDocuments().stream().filter(d->Objects.equals(d.getDocumentType().getAssignationType(), DocumentType.PREPARATORY_DOCUMENTATION)).count()));
        node.put("_closingDocumentations", String.format("%d di %d", dossier.getDocuments().stream().filter(d->(Objects.equals(d.getDocumentType().getAssignationType(), DocumentType.PRIVATE_CLOSING_DOCUMENTATION) || Objects.equals(d.getDocumentType().getAssignationType(), DocumentType.PUBLIC_CLOSING_DOCUMENTATION)) && d.getFile() != null && d.isValid()).count(), dossier.getDocuments().stream().filter(d->(Objects.equals(d.getDocumentType().getAssignationType(), DocumentType.PRIVATE_CLOSING_DOCUMENTATION) || Objects.equals(d.getDocumentType().getAssignationType(), DocumentType.PUBLIC_CLOSING_DOCUMENTATION))).count()));
        node.putPOJO("documents", dossier.getDocuments());
    }
}
