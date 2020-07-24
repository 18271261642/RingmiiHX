package com.guider.health.apilib.model;

import java.util.Date;

public class HealthAdvice extends TEntityHealth {
    String type;
    String content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public HealthAdvice(Date testTime, String type, String content) {
        setCreateTime(testTime);
        setType(type);
        setContent(content);
    }
}
