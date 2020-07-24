package com.guider.health.apilib;

import com.guider.health.apilib.enums.Code;

public class Result<T> {
    private Code code;
    private String msg;
    private T data;

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
