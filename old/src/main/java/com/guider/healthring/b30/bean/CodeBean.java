package com.guider.healthring.b30.bean;

public class CodeBean {


    /**
     * httpStatus : 400
     * error : China phone to send SMS when the foreign area code.
     * status : 457
     */

    private int httpStatus;
    private String error;
    private int status;

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
