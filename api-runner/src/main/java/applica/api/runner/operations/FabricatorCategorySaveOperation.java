package applica.api.runner.operations;

import applica.api.domain.model.users.categories.FabricatorCategory;
import applica.framework.Entity;
import org.springframework.stereotype.Component;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class FabricatorCategorySaveOperation extends EntityCodedBaseSaveOperation {

    @Override
    public Class<? extends Entity> getEntityType() {
        return FabricatorCategory.class;
    }

}
