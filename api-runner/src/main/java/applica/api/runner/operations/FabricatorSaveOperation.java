package applica.api.runner.operations;

import applica.api.domain.model.geo.GeoCity;
import applica.api.domain.model.users.Fabricator;
import applica.api.runner.facade.AccountFacade;
import applica.framework.Entity;
import applica.framework.Repo;
import applica.framework.widgets.operations.BaseSaveOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class FabricatorSaveOperation extends BaseSaveOperation {

    @Autowired
    private AccountFacade accountFacade;

    @Override
    public Class<? extends Entity> getEntityType() {
        return Fabricator.class;
    }

    @Override
    protected void finishEntity(ObjectNode node, Entity entity) {

        Fabricator fabricator = ((Fabricator) entity);

        if (node.get("_address") != null && !node.get("_address").isNull())
            fabricator.getAddress().setAddress(StringUtils.hasLength(node.get("_address").asText()) ? node.get("_address").asText() : null);
        if (node.get("_streetNumber") != null && !node.get("_streetNumber").isNull())
            fabricator.getAddress().setStreetNumber(StringUtils.hasLength(node.get("_streetNumber").asText()) ? node.get("_streetNumber").asText() : null);

        if (node.get("_city") != null && !node.get("_city").isNull()){

            if (node.get("_city").isObject()){
                fabricator.getAddress().setCity(Repo.of(GeoCity.class).get(node.get("_city").get("id").asText()).orElse(null));
            }
        }

        if (node.get("_category") != null){
            fabricator.setCategoryId(node.get("_category").get("id").asText());
        }
    }

}
