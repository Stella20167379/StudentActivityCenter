package com.example.graduatedesign.utils;

import android.content.Context;

import com.example.graduatedesign.data.model.College;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

//调用到AssetsUtil.java来读取assets下的json文件后转化实体类
public class AssetsUtil {

    public static List<College> getColleges(Context context) {
        InputStream is = null;
        ByteArrayOutputStream bos = null;
        try {
            is = context.getAssets().open("college.json");
            bos = new ByteArrayOutputStream();
            byte[] bytes = new byte[4 * 1024];
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            final String json = new String(bos.toByteArray());
            Gson gson = new Gson();
            final List<College> collegeList = gson.fromJson(json,new TypeToken<List<College>>(){}.getType());

            return collegeList;

        } catch (Exception e) {
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

