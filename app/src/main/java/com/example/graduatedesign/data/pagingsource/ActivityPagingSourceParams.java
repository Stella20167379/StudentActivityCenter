package com.example.graduatedesign.data.pagingsource;

import android.util.ArrayMap;

/**
 * 分页查询时，使用的全局单例参数
 */
public class ActivityPagingSourceParams {

    /**
     * 单例模式及volatile关键词
     * 参考 https://blog.csdn.net/qq594913801/article/details/85248286
     */
    private static volatile ArrayMap<String, Object> INSTANCE;

    public static ArrayMap<String, Object> getParams() {
        if (INSTANCE == null) {
            synchronized (ActivityPagingSourceParams.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ArrayMap<>();
                    //默认值
                    INSTANCE.put("schoolId", -1);
                }
            }
        }
        return INSTANCE;
    }
}
