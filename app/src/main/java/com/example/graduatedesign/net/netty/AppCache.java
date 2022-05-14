package com.example.graduatedesign.net.netty;


import com.example.graduatedesign.net.netty.model.LoginInfo;

/**
 * 缓存socket连接服务对象和登录信息的类
 */
public class AppCache {
    private static PushService service;
    private static LoginInfo myInfo;

    public static PushService getService() {
        return service;
    }

    public static void setService(PushService service) {
        AppCache.service = service;
    }

    public static LoginInfo getMyInfo() {
        return myInfo;
    }

    public static void setMyInfo(LoginInfo myInfo) {
        AppCache.myInfo = myInfo;
    }
}
