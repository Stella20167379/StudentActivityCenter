package com.example.graduatedesign.utils;

import java.util.Map;

public class DataUtil {

    /**
     * 后端返回的int数据被Gson转换器转换为了Double类型，一直没解决这个问题
     * @param data 网络请求响应负载的数据
     * @param key 要获取的整形字段
     * @return
     */
    public static int getIntFromGsonMap(Map<String,Object> data,String key){
        Double value = (Double) data.get(key);
        return value.intValue();
    }
}
