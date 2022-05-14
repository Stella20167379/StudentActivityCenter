package com.example.graduatedesign.utils;

import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import autodispose2.AutoDispose;
import autodispose2.AutoDisposeConverter;
import autodispose2.android.ViewScopeProvider;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public class RxLifecycleUtils {
    private RxLifecycleUtils(){
        throw new IllegalStateException("RxLifecycleUtils对象创建失败");
    }

    public static <T> AutoDisposeConverter<T> bindLifecycle(View view){
        //AutoDispose.autoDisposable(ViewScopeProvider.from(view));
        return AutoDispose.autoDisposable(ViewScopeProvider.from(view));
    }

    public static <T> AutoDisposeConverter<T> bindLifecycle(LifecycleOwner lifecycleOwner) {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner));
    }
}
