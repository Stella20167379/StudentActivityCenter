package com.example.graduatedesign.utils;

import android.util.ArrayMap;

import androidx.annotation.NonNull;

import java.util.Map;

/**
 * todo:对小数据量，使用arrayMap
 * 参考 https://blog.csdn.net/rzleilei/article/details/51658938
 * 参考 https://blog.csdn.net/kidari/article/details/99829437
 * 生成 Map<String,Object>类型的 HashMap 对象
 * 为了实现链式调用而采取了建造者模式，最终通过 {@link #build()} 获取建造的map对象
 */

public class ArrayMapBuilder {
    private final Map<String, Object> map = new ArrayMap<>();

    public ArrayMapBuilder put(@NonNull String key, Object value) {
        assert key != null;
        map.put(key, value);
        return this;
    }

    public Map<String, Object> build() {
        return map;
    }
}
