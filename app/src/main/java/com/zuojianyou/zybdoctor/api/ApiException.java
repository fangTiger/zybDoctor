package com.zuojianyou.zybdoctor.api;

import java.io.IOException;

public class ApiException extends IOException {

    private String code;
    private String errMsg;

    public ApiException(String msg) {
        super(msg);
    }

    public ApiException(String msg, String code) {
        super(msg);
        this.errMsg = msg;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return errMsg;
    }

    public void setMsg(String msg) {
        this.errMsg = msg;
    }
}
