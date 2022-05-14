package com.example.graduatedesign.net;

public class MyException extends RuntimeException {
    private int code;
    private String msg;

    public MyException() {
        super();
    }

    public MyException(int code) {
        super();
        this.code = code;
    }

    public MyException(int code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
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


}
