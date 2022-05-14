package com.example.graduatedesign.utils.beanmap;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

@Deprecated
public class MySimpleBeanMap {
    private Object bean;

    public static MySimpleBeanMap create(final Object obj) {
        return null;
    }

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

}
