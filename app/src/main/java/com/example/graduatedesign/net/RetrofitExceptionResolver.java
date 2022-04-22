package com.example.graduatedesign.net;


import android.net.ParseException;

import com.example.graduatedesign.data.model.NetResult;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.HttpException;

public class RetrofitExceptionResolver {

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    public static String resolve(Throwable e) {

        if (e instanceof HttpException) {
            String msg;
            HttpException httpException = (HttpException) e;
            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    msg = "网络错误";
                    break;
            }
            return msg;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            return "解析错误";
        } else if (e instanceof ConnectException
                || e instanceof SocketTimeoutException
                || e instanceof UnknownHostException) {
            return "连接失败";
        } else if (e instanceof SSLHandshakeException) {
            return "证书验证失败";
        } else {
            return e.getMessage();
        }
    }

    public static void analyzeNetResult(NetResult netResult) {
        if (netResult==null)
            throw new IllegalArgumentException("收到空数据，可能是解析错误或后台bug！");
        switch (netResult.getCode()){
            case 201:
            case 200:
                return;
            default:
                throw new IllegalStateException(netResult.getMsg());
        }
    }

}