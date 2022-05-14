package com.example.graduatedesign.net.netty.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by hzwangchenyan on 2017/12/26.
 * 登录信息
 */
public class LoginInfo implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("token")
    private String token;

    public LoginInfo() {
    }

    public LoginInfo(int id, String token) {
        this.id = id;
        this.token = token;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
