package com.guider.health.apilib.model;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HealthAdvice extends TEntityHealth {
    String type;
    String content;

    public HealthAdvice(Date testTime, String type, String content) {
        setCreateTime(testTime);
        setType(type);
        setContent(content);
    }
}
