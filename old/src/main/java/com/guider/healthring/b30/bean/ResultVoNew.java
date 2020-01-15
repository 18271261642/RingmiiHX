package com.guider.healthring.b30.bean;

public class ResultVoNew extends BaseResultVoNew{

    /**
     * code : 0
     * msg : 成功
     * data : {"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzMiIsImlkIjozMiwiZXhwIjoxNTYxMDI2NDg4LCJpYXQiOjE1NjEwMTkyODgsImp0aSI6IjE5YmMwNWYxLWExMTEtNDQyNi1hNTJjLTkzYjU3ZTgzZGFiYiJ9.zuqN_XcFnA8bLJLFzEPn5urG4B32pkw0DGy5baguCGo","refreshToken":"KzJzJC3BXjP7zwij6GUGVT3KxaQH62VV","accountId":32}
     */

    private ResultVoNew.DataBean data;

//    public int getCode() {
//        return code;
//    }
//
//    public void setCode(int code) {
//        this.code = code;
//    }
//
//    public String getMsg() {
//        return msg;
//    }
//
//    public void setMsg(String msg) {
//        this.msg = msg;
//    }

    public ResultVoNew.DataBean getData() {
        return data;
    }

    public void setData(ResultVoNew.DataBean data) {
        this.data = data;
    }

    public class DataBean {
        /**
         * token : eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzMiIsImlkIjozMiwiZXhwIjoxNTYxMDI2NDg4LCJpYXQiOjE1NjEwMTkyODgsImp0aSI6IjE5YmMwNWYxLWExMTEtNDQyNi1hNTJjLTkzYjU3ZTgzZGFiYiJ9.zuqN_XcFnA8bLJLFzEPn5urG4B32pkw0DGy5baguCGo
         * refreshToken : KzJzJC3BXjP7zwij6GUGVT3KxaQH62VV
         * accountId : 32
         */

        private String token;
        private String refreshToken;
        private int accountId;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public int getAccountId() {
            return accountId;
        }

        public void setAccountId(int accountId) {
            this.accountId = accountId;
        }
    }
}