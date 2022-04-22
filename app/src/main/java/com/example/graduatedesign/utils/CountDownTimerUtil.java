package com.example.graduatedesign.utils;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;

/**
 * 获取验证码按钮倒计时设置
 */
public class CountDownTimerUtil extends CountDownTimer {
    private final Button button;

    public CountDownTimerUtil(Button button, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.button = button;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        button.setEnabled(false); //设置不可点击
        button.setTextColor(Color.BLACK);
        String btnTxt = millisUntilFinished / 1000 + "秒后可重新发送";
        SpannableString spannableString = new SpannableString(btnTxt);  //获取按钮上的文字
        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
        spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//将倒计时的时间设置为红色
        button.setText(spannableString);
    }

    @Override
    public void onFinish() {
        button.setText("重新获取验证码");
        button.setTextColor(Color.WHITE);
        button.setEnabled(true);//重新获得点击
    }

    /**
     * 发生异常时，手动取消计时器，使用户能重新获取验证码
     *
     */
    public void cancelTimerCount(){
        this.cancel();
        button.setText("重新获取验证码");
        button.setTextColor(Color.WHITE);
        button.setEnabled(true);//重新获得点击
    }
}
