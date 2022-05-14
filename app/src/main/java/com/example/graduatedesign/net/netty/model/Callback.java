package com.example.graduatedesign.net.netty.model;

/**
 * 通用回调，符合服务器返回的统一格式
 */
public interface Callback<T> {
    /**
     * @param code 结果代码,700代表收到消息但转换失败等
     * @param msg  结果提示信息
     * @param t    结果中携带的数据
     */
    void onEvent(int code, String msg, T t);
}
