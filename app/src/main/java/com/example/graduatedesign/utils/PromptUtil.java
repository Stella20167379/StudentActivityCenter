package com.example.graduatedesign.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

public class PromptUtil {

    /**
     * Snackbar底部弹出阻止用户点击导航按钮，且不够明显
     *
     * @deprecated 如果view被销毁，此方法会引起异常导致闪退，说引用空对象之类的
     */
    @Deprecated
    public static void snackbarShowTxt(View view, Integer txtResId) {

        Snackbar.make(view, txtResId,
                Snackbar.LENGTH_SHORT)
                .show();
    }

    /**
     * Snackbar底部弹出阻止用户点击导航按钮，且不够明显
     *
     * @deprecated 如果view被销毁，此方法会引起异常导致闪退，说引用空对象之类的
     */
    @Deprecated
    public static void snackbarShowTxt(View view, String txt) {
        Snackbar.make(view, txt,
                Snackbar.LENGTH_SHORT)
                .show();
    }

    public static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    public static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton("确定", null)
                .setOnDismissListener(onDismiss)
                .show();
    }

    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

}
