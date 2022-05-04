package com.example.graduatedesign.student_activity_module.ui.detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.student_activity_module.ui.detail.ActivityDetailHolderFragment;

public class ActivityDetailPresenter implements DefaultLifecycleObserver {
    private ActivityDetailHolderFragment.ActivityDetailFragment view;

    public ActivityDetailPresenter(ActivityDetailHolderFragment.ActivityDetailFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        view=null;
    }

    public void initDetail(MyRepository repository,int activityId){

    }

    /**
     * 用户报名活动
     * @param userId 用户id
     * @param activityId 活动id
     */
    public void signToActivity(int userId,MyRepository repository,int activityId){

    }
}
