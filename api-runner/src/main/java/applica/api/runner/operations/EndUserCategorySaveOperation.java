package applica.api.runner.operations;

import applica.api.domain.model.users.categories.EndUserCategory;
import applica.framework.Entity;
import org.springframework.stereotype.Component;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class EndUserCategorySaveOperation extends EntityCodedBaseSaveOperation {

    @Override
    public Class<? extends Entity> getEntityType() {
        return EndUserCategory.class;
    }

}
