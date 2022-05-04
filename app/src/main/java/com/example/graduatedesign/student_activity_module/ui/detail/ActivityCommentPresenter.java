package com.example.graduatedesign.student_activity_module.ui.detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.student_activity_module.data.Comment;
import com.example.graduatedesign.student_activity_module.ui.detail.ActivityDetailHolderFragment;

public class ActivityCommentPresenter implements DefaultLifecycleObserver {
    private ActivityDetailHolderFragment.ActivityCommentFragment view;

    public ActivityCommentPresenter(ActivityDetailHolderFragment.ActivityCommentFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        view=null;
    }

    public void getComments(MyRepository repository, int activityId){

    }

    public void submitComment(MyRepository repository, Comment comment){

    }

}
