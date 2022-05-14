package com.example.graduatedesign.personal_module.ui.comment;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.utils.RxLifecycleUtils;

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
        repository.getCommentsForUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(comments -> {
                            if (comments == null || comments.size() < 1)
                                view.onInitFail();
                            else view.onInitSuccess(comments);
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            view.onInitFail();
                        });
    }

}
