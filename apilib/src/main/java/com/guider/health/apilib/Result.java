package com.guider.health.apilib;

import com.guider.health.apilib.enums.Code;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Result<T> {
    private Code code;
    private String msg;
    private T data;
}
