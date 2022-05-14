package com.example.graduatedesign.net;


import android.net.ParseException;

import com.example.graduatedesign.data.model.NetResult;
import com.google.gson.JsonParseException;

import org.json.JSONException;

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
                    msg = "未授权";
                    break;
                case FORBIDDEN:
                    msg = "服务器拒绝了请求";
                    break;
                case NOT_FOUND:
                    msg = "找不到对应的服务器";
                    break;
                case REQUEST_TIMEOUT:
                    msg = "连接超时";
                    break;
                case SERVICE_UNAVAILABLE:
                case INTERNAL_SERVER_ERROR:
                    msg = httpException.getMessage();
                    break;
                case GATEWAY_TIMEOUT:
                case BAD_GATEWAY:
                    msg = "网关错误";
                    break;
                default:
                    msg = "网络错误";
                    break;
            }
            return msg;
        } else if (e instanceof MyException) {
            return e.getMessage();
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
        switch (netResult.getCode()) {
            case 201:
            case 200:
                return;
            case 707:
                throw new MyException(707, "登录失效！");
            default:
                throw new MyException(netResult.getCode(), netResult.getMsg());
        }
    }

}