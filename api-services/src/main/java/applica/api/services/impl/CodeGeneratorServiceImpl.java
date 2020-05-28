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

    @Override
    public long getFirstAvailableCode(Class<? extends NumericCodedEntity> codeEntity) {
        Entity lastCode = Repo.of(codeEntity).find(Query.build().page(1).rowsPerPage(1).sort(Filters.CODE, true)).findFirst().orElse(null);
        if (lastCode == null)
            return 1;
        else
            return ((NumericCodedEntity) lastCode).getCode() + 1;
    }

}
