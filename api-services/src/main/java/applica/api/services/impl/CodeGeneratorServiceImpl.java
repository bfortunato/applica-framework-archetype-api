package applica.api.services.impl;

import applica.api.domain.model.Filters;
import applica.framework.Entity;
import applica.framework.Query;
import applica.framework.Repo;
import applica.framework.security.CodeGeneratorService;
import applica.framework.security.NumericCodedEntity;
import org.springframework.stereotype.Service;

@Service
public class CodeGeneratorServiceImpl implements CodeGeneratorService {

    public long getFirstAvailableCode(Class<? extends NumericCodedEntity> codeEntity) {
        return getFirstAvailableCode(codeEntity, Query.build().page(1).rowsPerPage(1).sort(Filters.CODE, true));
    }

    @Override
    public long getFirstAvailableCode(Class<? extends NumericCodedEntity> codeEntity, Query query) {
        Entity lastCode = Repo.of(codeEntity).find(query).findFirst().orElse(null);
        if (lastCode == null)
            return 1;
        else
            return ((NumericCodedEntity) lastCode).getCode() + 1;
    }

}
