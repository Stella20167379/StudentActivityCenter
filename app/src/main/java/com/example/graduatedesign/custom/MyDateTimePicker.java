package com.example.graduatedesign.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.graduatedesign.R;
import com.example.graduatedesign.utils.DataUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;


public class MyDateTimePicker extends DialogFragment implements NumberPicker.OnValueChangeListener {
    private static final String TAG = "MyDateTimePicker";
    private NumberPicker yearPicker;
    private NumberPicker monthPicker;
    private NumberPicker datePicker;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface MyDateTimePickerDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String result);

        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    private MyDateTimePickerDialogListener listener;
    private String title = "日期时间选择";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_date_time_picker, null);

        Calendar calendar = Calendar.getInstance();
        yearPicker = view.findViewById(R.id.number_picker_year);
        monthPicker = view.findViewById(R.id.number_picker_month);
        datePicker = view.findViewById(R.id.number_picker_date);
        hourPicker = view.findViewById(R.id.number_picker_hour);
        minutePicker = view.findViewById(R.id.number_picker_minute);

        //限制年份范围,我估计日历获取的年也从0开始
        int yearNow = calendar.get(Calendar.YEAR);
        yearPicker.setMinValue(yearNow);
        yearPicker.setMaxValue(yearNow + 1);
        yearPicker.setValue(yearNow);
        yearPicker.setWrapSelectorWheel(false);  //关闭选择器循环

        //设置月份范围为1~12,日历获取的月份从0开始
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(calendar.get(Calendar.MONTH) + 1);
        monthPicker.setWrapSelectorWheel(false);

        //日期限制存在变化，需要根据当月最大天数来调整
        datePicker.setMinValue(1);
        datePicker.setMaxValue(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        datePicker.setValue(calendar.get(Calendar.DATE));
        datePicker.setWrapSelectorWheel(false);

        //24小时制，限制小时数为5~22
        hourPicker.setMinValue(5);
        hourPicker.setMaxValue(22);
        hourPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        hourPicker.setWrapSelectorWheel(false);

        //限制分钟数为0~59
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(calendar.get(Calendar.MINUTE));
        minutePicker.setWrapSelectorWheel(false);

        //为年份和月份设置监听
        yearPicker.setOnValueChangedListener(this);
        monthPicker.setOnValueChangedListener(this);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(R.string.txt_confirm, (dialog, id) -> {
                    if (listener != null)
                        listener.onDialogPositiveClick(MyDateTimePicker.this, buildDateTimeStr());
                })
                .setNegativeButton(R.string.txt_cancel, (dialog, id) -> {
                    if (listener != null)
                        listener.onDialogNegativeClick(MyDateTimePicker.this);
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setListener(MyDateTimePickerDialogListener listener) {
        this.listener = listener;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 改变当前选择器的最小日期时间
     *
     * @param minDatetime 最低时间的字符串形式
     */
    public void setMinDatetime(String minDatetime) {
        LocalDateTime dateTime = DataUtil.dateFromString(minDatetime, true);
        if (dateTime==null){
            Log.d(TAG, "setMinDatetime: 转换所得日期时间为空");
            return;
        }
        yearPicker.setMinValue(dateTime.getYear());
        monthPicker.setMinValue(dateTime.getMonthValue());
        datePicker.setMinValue(dateTime.getDayOfMonth());
        hourPicker.setMinValue(dateTime.getHour());
        minutePicker.setMinValue(dateTime.getMinute());
    }

    /**
     * 本处是给年、月选择器添加的值变化监听器，使日期能随着年月正确显示区间
     *
     * @param picker 监听对象
     * @param oldVal 原值
     * @param newVal 新值
     */
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        String dateStr = String.format(Locale.CHINA, "%d-%d", yearPicker.getValue(), monthPicker.getValue());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DataUtil.YMPattern, Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dateValue = datePicker.getValue();
        int maxValue = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        datePicker.setMaxValue(maxValue);
        //重设日期值，防止月份变动时超过最大值
        datePicker.setValue(Math.min(dateValue, maxValue));
    }

    /**
     * 将用户选中的年月日时间转换成字符串
     */
    private String buildDateTimeStr() {
        String result = String.format(Locale.CHINA, "%d-%d-%d %d:%d",
                yearPicker.getValue(), monthPicker.getValue(), datePicker.getValue(),
                hourPicker.getValue(), minutePicker.getValue());

        return result;
    }

}
