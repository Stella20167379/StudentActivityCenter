package com.example.graduatedesign.net;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class MyTokenInterceptor implements Interceptor {

    private String token;

    public MyTokenInterceptor(String token){
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        //拿到请求
        Request originalRequest = chain.request();
        Request.Builder builder=originalRequest.newBuilder()
                .header("token",token);

        Request request=builder.build();
        //返回处理后的响应
        return chain.proceed(request);
    }
}
