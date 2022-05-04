package com.example.graduatedesign.personal_module.ui.comment;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyCommentPresenter implements DefaultLifecycleObserver {
    private MyCommentFragment view;

    public MyCommentPresenter(MyCommentFragment view){
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        //删除对框架类fragment的引用,系统才能成功回收资源
        view=null;
    }

    public void initData(MyRepository repository,int userId){
        repository.getMyComments(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(payRecords -> view.onInitSuccess(payRecords),
                        throwable -> throwable.printStackTrace());
    }

}
