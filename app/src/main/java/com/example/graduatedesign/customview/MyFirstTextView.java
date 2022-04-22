package com.example.graduatedesign.customview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;

public class MyFirstTextView extends MaterialTextView {
    private Paint mPaint1,mPaint2;

    public MyFirstTextView(@NonNull Context context) {
        super(context);
    }

    public MyFirstTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFirstTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setTextAppearance(@NonNull Context context, int resId) {
        super.setTextAppearance(context, resId);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initView();

//        两个长宽不等的矩形,覆盖形成边框
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint1);
        canvas.drawRect(10, 10, getMeasuredWidth() - 10,
                getMeasuredHeight() - 10, mPaint2);
        canvas.save();
//        x轴上右移10个单位
        canvas.translate(10, 0);
        super.onDraw(canvas);
//        继承的方法后面，画的是背景
        canvas.restore();
    }

    private void initView() {
        mPaint1 = new Paint();
        mPaint1.setColor(Color.RED);
        mPaint1.setStyle(Paint.Style.FILL);

        mPaint2 = new Paint();
        mPaint2.setColor(Color.YELLOW);
        mPaint2.setStyle(Paint.Style.FILL);
    }
}
