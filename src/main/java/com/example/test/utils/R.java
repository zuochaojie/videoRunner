package com.example.test.utils;

import java.util.Map;

public class R {
    private int code;
    private String msg;
    private Object data;

    public R() {
        this.code = 0;
        this.msg = "success";
    }

    public static R error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(500, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.setMsg(msg);
        r.setCode(code);
        return r;
    }

    public static R ok(String msg) {
        R r = new R();
        r.setMsg(msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.setData(map);
        return r;
    }

    public static R ok() {
        return new R();
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public R put(Object data) {
        this.data = data;
        return this;
    }
}
