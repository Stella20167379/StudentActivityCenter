package com.example.graduatedesign.message_module.ui.enter_apply;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AssociationEnterApplyPresenter implements DefaultLifecycleObserver {
    private AssociationEnterApplyFragment view;

    public AssociationEnterApplyPresenter(AssociationEnterApplyFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        //删除对框架类fragment的引用,系统才能成功回收资源
        view=null;
    }

    /**
     * 消息模块中，社团管理员查询有无未处理的入会申请
     * @param userId 当前用户id
     */
    public void initData(MyRepository repository, int userId){
        repository.getAssociationMembers(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(members -> {
                        },
                        throwable -> throwable.printStackTrace());
    }

    public void passEnterApply(){}

    public void denyEnterApply(){}

}
