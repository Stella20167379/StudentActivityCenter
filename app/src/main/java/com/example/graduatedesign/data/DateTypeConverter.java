package com.example.graduatedesign.data;

import androidx.room.TypeConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTypeConverter {
    //具有转换功能的对象
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/mm/dd");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/mm/dd HH:mm:ss");

    public static LocalDateTime dateFromString(String value,boolean withTimeOrNot) {
        if (value == null)
            return null;
        LocalDateTime time;
        if (withTimeOrNot)
            time = LocalDateTime.parse(value, dateTimeFormatter);
        else time=LocalDateTime.parse(value,dateFormatter);
        return time;
    }

    public static String dateToString(LocalDateTime value,boolean withTimeOrNot) {
        if (value == null)
            return null;

        String time;
        if (withTimeOrNot)
            time = dateTimeFormatter.format(value);
        else time=dateFormatter.format(value);

        return time;
    }
}
