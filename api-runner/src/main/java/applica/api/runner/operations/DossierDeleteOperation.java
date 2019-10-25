package applica.api.runner.operations;

import applica.api.domain.model.dossiers.Dossier;
import applica.api.services.DossiersService;
import applica.api.services.exceptions.DossierNotFoundException;
import applica.framework.Entity;
import applica.framework.widgets.operations.BaseDeleteOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Created by bimbobruno on 24/01/2017.
 */

@Component
public class DossierDeleteOperation extends BaseDeleteOperation {

    @Autowired
    private DossiersService dossiersService;

    @Override
    public void delete(List<String> ids) {
        ids.forEach(id -> {
            try {
                dossiersService.delete(id);
            } catch (DossierNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return Dossier.class;
    }


}
