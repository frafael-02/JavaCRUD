package tvz.java.rafaelprojekt.entity;

import java.io.Serializable;

public enum Server implements Serializable {
    EUROPE("EU"), NORTH_AMERICA("NA"), LATIN_AMERICA("LAM");

    private String code;

    Server(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
