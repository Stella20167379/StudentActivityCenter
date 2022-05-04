package com.example.graduatedesign;

import org.junit.Test;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.room.Room;

import com.example.graduatedesign.data.MyDatabase;
import com.example.graduatedesign.personal_module.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public void mapToBeanTest(){
        Map<String,Object> data=new HashMap<>();
        data.put("id",11);
        data.put("nickname","zhangsan");
        User user= (User) data;
        System.out.println(user.getNickname());
    }

}