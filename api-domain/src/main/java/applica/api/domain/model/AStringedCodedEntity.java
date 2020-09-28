package applica.api.domain.model;

import applica.framework.AEntity;
import applica.framework.widgets.annotations.Search;
import applica.framework.widgets.annotations.Validation;
/**
 * Created by antoniolovicario on 18/10/17.
 */

public abstract class AStringedCodedEntity extends AEntity implements StringifiedCodedEntity {

    @Search(includeInKeyword = true)
    @Validation(required = true, unique = true)
    private String code;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", getCode(), entityDescription());
    }

    public String entityDescription() {
        return "";
    }

    public String getFullDescription() {
        return toString();
    }
}
