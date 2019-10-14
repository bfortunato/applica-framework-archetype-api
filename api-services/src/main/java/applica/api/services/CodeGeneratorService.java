package applica.api.services;

import applica.framework.Entity;

public interface CodeGeneratorService {

    long getFirstAvailableCode(Class<? extends Entity> codeEntity);

}
