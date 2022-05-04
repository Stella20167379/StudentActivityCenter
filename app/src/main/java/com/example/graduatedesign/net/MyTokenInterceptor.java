package com.example.graduatedesign.net;

import androidx.annotation.NonNull;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Singleton
public class MyTokenInterceptor implements Interceptor {
    private String tokenName="satoken";
    private String token="this is token";

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Inject
    public MyTokenInterceptor(){}

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        //拿到请求
        Request originalRequest = chain.request();
        Request.Builder builder=originalRequest.newBuilder()
                .header(tokenName,token);

        Request request=builder.build();
        //返回处理后的响应
        return chain.proceed(request);
    }
}
