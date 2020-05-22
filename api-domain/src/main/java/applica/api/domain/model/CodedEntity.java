package applica.api.domain.model;

import applica.framework.AEntity;

public abstract class CodedEntity extends AEntity {

    private long code;

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

}