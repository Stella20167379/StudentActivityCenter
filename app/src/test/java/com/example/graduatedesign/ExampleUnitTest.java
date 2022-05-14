package com.example.graduatedesign;

import static org.junit.Assert.assertEquals;

import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.personal_module.data.User;
import com.google.gson.Gson;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void mapToBeanTest() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", 11);
        data.put("nickname", "zhangsan");
        User user = (User) data;
        System.out.println(user.getNickname());
    }

    @Test
    public void strSplitTest() {
        String separator = "_";
        String str01 = "+";
        String str02 = "+11_2_+";
        String str03 = "+11_2_zheshi+";

        String[] str01Split = str01.split(separator);
        String[] str02Split = str02.split(separator);
        String[] str03Split = str03.split(separator);

        System.out.println("------------------------------------------");
        for (String s : str01Split) {
            System.out.println("str01Split:" + s);
        }
        System.out.println("------------------------------------------");
        for (String s : str02Split) {
            System.out.println("str02Split:" + s);
        }
        System.out.println("------------------------------------------");
        for (String s : str03Split) {
            System.out.println("str03Split:" + s);
        }
        System.out.println("------------------------------------------");
    }

    @Test
    public void subStrTest() {
        String old = "2022-04-28 08:00:00";
        System.out.println("截取后：" + old.substring(0, 10));
    }

    @Test
    public void gsonTest() {
        MyStudentActivity activity = new MyStudentActivity();
        activity.setLocation("这是一个地点");
        activity.setTitle("标题");
        Gson gson = new Gson();
        String jsonStr = gson.toJson(activity);
        System.out.println(jsonStr);
    }

}