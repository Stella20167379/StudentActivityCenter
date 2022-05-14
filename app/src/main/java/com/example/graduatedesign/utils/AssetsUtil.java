package com.example.graduatedesign.utils;

import android.content.Context;

import com.google.gson.JsonParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetsUtil {

    /**
     * 获取assets下的json文件，转换为字符串形式返回内容
     *
     * @param context
     * @param fileName 文件名称
     */
    public static String getJsonFromAssets(Context context, String fileName) {
        InputStream is = null;
        ByteArrayOutputStream bos = null;
        try {
            is = context.getAssets().open(fileName);
            bos = new ByteArrayOutputStream();
            byte[] bytes = new byte[4 * 1024];
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            final String json = new String(bos.toByteArray());
            return json;

        } catch (Exception e) {
            System.out.println("发生了错误");
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (JsonParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

