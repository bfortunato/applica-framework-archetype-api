package applica.api.runner.operations;

import applica.api.domain.model.auth.User;
import applica.api.domain.model.users.Fabricator;
import applica.api.domain.utils.CustomUtils;
import applica.api.services.DocumentsService;
import applica.api.services.responses.ResponseCode;
import applica.framework.Entity;
import applica.framework.widgets.operations.OperationException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FabricatorSaveOperation extends EntityCodedBaseSaveOperation {

    @Autowired
    private DocumentsService documentsService;

    @Override
    public Class<? extends Entity> getEntityType() {
        return Fabricator.class;
    }

    @Override
    protected void finishEntity(ObjectNode node, Entity entity) {

        Fabricator fabricator = ((Fabricator) entity);

//        if (node.get("_address") != null && !node.get("_address").isNull())
//            fabricator.getAddress().setAddress(StringUtils.hasLength(node.get("_address").asText()) ? node.get("_address").asText() : null);
//        if (node.get("_streetNumber") != null && !node.get("_streetNumber").isNull())
//            fabricator.getAddress().setStreetNumber(StringUtils.hasLength(node.get("_streetNumber").asText()) ? node.get("_streetNumber").asText() : null);
//
//        if (node.get("_city") != null && !node.get("_city").isNull()){
//
//            if (node.get("_city").isObject()){
//                fabricator.getAddress().setCity(Repo.of(GeoCity.class).get(node.get("_city").get("id").asText()).orElse(null));
//            }
//        }

        fabricator.getAddress().setCountry(node.get("_country") != null ? node.get("_country").asText() : null);
        fabricator.getAddress().setRegion(node.get("_region") != null ? node.get("_region").asText() : null);
        fabricator.getAddress().setProvince(node.get("_province") != null ? node.get("_province").asText() : null);
        fabricator.getAddress().setMunicipality(node.get("_municipality") != null ? node.get("_municipality").asText() : null);
        fabricator.getAddress().setPostalCode(node.get("_postalCode") != null ?node.get("_postalCode").asText() : null);
        fabricator.getAddress().setAddress(node.get("_address") != null ? node.get("_address").asText() : null);
        fabricator.getAddress().setStreetNumber(node.get("_streetNumber") != null ? node.get("_streetNumber").asText() : null);

        if (node.get("_category") != null){
            fabricator.setCategoryId(node.get("_category").get("id").asText());
        }
    }

    @Override
    protected void beforeSave(ObjectNode node, Entity entity) throws OperationException {
        super.beforeSave(node, entity);
        if (node.get("mail") == null || node.get("mail").isNull() || node.get("password") == null || node.get("password").isNull()){
            throw new OperationException(ResponseCode.ERROR_MAIL_AND_PASSWORD_REQUIRED);
        } else {
            User user = CustomUtils.createUserFromPerson(node);
            ((Fabricator) entity).setUserId(user.getId());
            ((Fabricator) entity).setDocuments(documentsService.generateFabricatorDocuments());
        }
    }

}
