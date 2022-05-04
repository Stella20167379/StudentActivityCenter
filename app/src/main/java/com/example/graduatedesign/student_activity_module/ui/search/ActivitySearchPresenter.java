package com.example.graduatedesign.student_activity_module.ui.search;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;

public class ActivitySearchPresenter implements DefaultLifecycleObserver {
    private ActivitySearchFragment view;

    public ActivitySearchPresenter(ActivitySearchFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        view=null;
    }

    public void searchActivities(MyRepository repository,String date){

    }
}
