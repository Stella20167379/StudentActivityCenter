package com.example.graduatedesign.data;

import androidx.room.TypeConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTypeConverter {
    //具有转换功能的对象
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @TypeConverter
    public static LocalDateTime dateFromString(String value) {
        if (value == null)
            return null;
        LocalDateTime time = LocalDateTime.parse(value, dateTimeFormatter);
        return time;
    }

    @TypeConverter
    public static String dateToString(LocalDateTime date) {
        if (date == null)
            return null;
        //2.要转换的对象-LocalDateTime对象
        //3.发动功能
        String time = dateTimeFormatter.format(date);
        return time;
    }
}
