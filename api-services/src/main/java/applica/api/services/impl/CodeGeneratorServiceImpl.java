package applica.api.services.impl;

import applica.api.domain.model.CodedEntity;
import applica.api.domain.model.Filters;
import applica.api.services.CodeGeneratorService;
import applica.framework.Entity;
import applica.framework.Query;
import applica.framework.Repo;
import org.springframework.stereotype.Service;

@Service
public class CodeGeneratorServiceImpl implements CodeGeneratorService {

    @Override
    public long getFirstAvailableCode(Class<? extends Entity> codeEntity) {
        Entity lastCode = Repo.of(codeEntity).find(Query.build().sort(Filters.CODE, true)).findFirst().orElse(null);
        if (lastCode == null)
            return 1;
        else
            return ((CodedEntity) lastCode).getCode() + 1;
    }

}
