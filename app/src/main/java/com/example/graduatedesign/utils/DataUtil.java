package com.example.graduatedesign.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class DataUtil {
    /**
     * 年月格式符
     */
    public static String YMPattern = "yyyy-MM";
    /**
     * 年月日格式符
     */
    public static String YMDPattern = "yyyy-MM-dd";
    /**
     * 年月日时分格式符
     * 见DateTimeFormatter最上方说明
     */
    public static String YMDHMPattern = "yyyy-MM-dd HH:mm:ss";

    //具有转换功能的对象
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(YMDPattern, Locale.CHINA);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(YMDHMPattern, Locale.CHINA);

    /**
     * 字符串转时间对象
     *
     * @param value         待转换的字符串
     * @param withTimeOrNot 是否带上时分
     * @return
     */
    public static LocalDateTime dateFromString(String value, boolean withTimeOrNot) {
        if (value == null)
            return null;
        LocalDateTime time;
        if (withTimeOrNot)
            time = LocalDateTime.parse(value, dateTimeFormatter);
        else time = LocalDateTime.parse(value, dateFormatter);
        return time;
    }

    /**
     * 时间对象转字符串
     * 由于后端传来的时间带有秒，故转换时间时也加入秒
     *
     * @param value         待转换的时间对象
     * @param withTimeOrNot 是否带有时分
     * @return
     */
    public static String dateToString(LocalDateTime value, boolean withTimeOrNot) {
        if (value == null)
            return null;

        String time;
        if (withTimeOrNot)
            time = dateTimeFormatter.format(value);
        else time = dateFormatter.format(value);

        return time;
    }

    /**
     * @param old 包含年月日，时分秒的时间字符串
     * @deprecated 去除时间字符串格中的时分秒
     */
    public static String formatDateString(String old) {
        //beginIndex – 起始索引（包括）、endIndex – 结束索引（不包括）
        return old.substring(0, 10);
    }


    /**
     * 时间参数一大于时间参数二时返回true
     *
     * @param var01 时间参数一
     * @param var02 时间参数二
     * @return
     */
    public static boolean compareDatetimeStr(String var01, String var02) {
        if (Long.valueOf(var01.replaceAll("[-\\s:]", "")) > Long.valueOf(var02.replaceAll("[-\\s:]", ""))) {
            //满足条件时表示：开始时间大于结束时间
            return true;
        }
        return false;
    }


    /**
     * 后端返回的int数据被Gson转换器转换为了Double类型，一直没解决这个问题
     *
     * @param data 网络请求响应负载的数据
     * @param key  要获取的整形字段
     * @return
     */
    public static int getIntFromGsonMap(Map<String, Object> data, String key) {
        Double value = (Double) data.get(key);
        return value.intValue();
    }

    /**
     * 返回拼接后的可以访问的图片路径
     *
     * @param imgPath 图片的相对路径
     * @return
     */
    @Deprecated
    public static String getImgDownloadUri(String imgPath) {
        return "http://10.0.2.2:7365/files/down/" + imgPath;
    }

    /**
     * 将map对象转换为实体类对象
     *
     * @param clazz 目标实体类的class
     */
    public static <T> T mapToBean(final Map<String, Object> map, final Class<?> clazz) {
        if (map == null)
            return null;
        T bean = null;
        try {
            bean = (T) clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        //获取到所有属性，不包括继承的属性
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //获取字段的修饰符
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            //设置对象的访问权限
            field.setAccessible(true);
            //根据属性名称去map获取value
            if (map.containsKey(field.getName())) {
                //给对象赋值
                try {
                    field.set(bean, map.get(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

    /**
     * JavaBean转Map
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> beanToMap(final Object obj) {
        Map<String, Object> map = new LinkedHashMap<>();
        Class<?> clazz = obj.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = null;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (value == null) {
                value = "";
            }
            map.put(fieldName, value);
        }
        return map;
    }

    /**
     * 生成包含文件的请求体，参数可为空
     *
     * @param file   包含文件的请求体部分
     * @param params 请求包含的参数,key、value都必须是字符类型
     */
    public static RequestBody buildRequestBody(final RequestBody file, final Map<String, String> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (file != null)
            builder.addFormDataPart("file", "img", file);
        if (params != null) {
            Iterator keyIt = params.keySet().iterator();
            while (keyIt.hasNext()) {
                String key = (String) keyIt.next();
                builder.addFormDataPart(key, params.get(key));
            }
        }
        return builder.build();
    }

}
