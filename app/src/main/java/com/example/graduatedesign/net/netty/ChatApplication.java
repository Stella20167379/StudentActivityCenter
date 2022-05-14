package com.example.graduatedesign.net.netty;

import android.app.Application;
import android.content.Intent;

/**
 * Created by hzwangchenyan on 2017/12/26.
 * 启动socket服务
 */
public class ChatApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, PushService.class));
    }
}
