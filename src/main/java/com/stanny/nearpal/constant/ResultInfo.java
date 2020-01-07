package com.stanny.nearpal.constant;

/**
 * Created by Xiangb on 2019/12/17.
 * 功能：
 */
public enum ResultInfo {

    SUCCESS(10000, "操作成功"),
    FAILED(10001, "操作失败"),
    TIMEOUT(10002, "登录超时");

    private int code;
    private String msg;

    ResultInfo(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
