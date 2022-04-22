package com.example.graduatedesign.utils;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class PromptUtil {

    public static void snackbarShowTxt(View view, Integer txtResId) {

        Snackbar.make(view, txtResId,
                Snackbar.LENGTH_SHORT)
                .show();
    }

    public static void snackbarShowTxt(View view, String txt) {
        Snackbar.make(view, txt,
                Snackbar.LENGTH_SHORT)
                .show();
    }
}
