package com.example.graduatedesign.data.model;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * 成功连接后台服务器后，服务器返回的结果
 * @param <T>
 */
public class NetResult<T> {
    private  @NonNull int code;
    private String msg;
    private T data;


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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}