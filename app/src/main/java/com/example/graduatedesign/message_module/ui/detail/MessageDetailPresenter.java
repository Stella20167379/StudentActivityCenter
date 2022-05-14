package com.example.graduatedesign.message_module.ui.detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class MessageDetailPresenter implements DefaultLifecycleObserver {
    private MessageDetailFragment view;

    public MessageDetailPresenter(MessageDetailFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        //删除对框架类fragment的引用,系统才能成功回收资源
        view = null;
    }
}
