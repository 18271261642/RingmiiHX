package com.guider.health.apilib.enums;

public enum Code {
    OK(0),
    FAILE(-1),
    UPLOADEROR(-2),
    NOSTUDIO(-3);

    private Integer id;

    private Code(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
}
