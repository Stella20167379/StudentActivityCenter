package com.example.graduatedesign.student_activity_module.ui.add;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.MyStudentActivity;

import java.io.File;
@Deprecated
public class ActivityAddPresenter implements DefaultLifecycleObserver {
    private ActivityAddFragment view;

    public ActivityAddPresenter(ActivityAddFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        view = null;
    }

    public void addActivity(MyRepository repository, MyStudentActivity activity, File file) {
        if (file == null && activity == null) {
            return;
        }


    }


}
