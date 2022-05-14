package com.example.graduatedesign.association_module.ui.show_members;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ShowMembersPresenter implements DefaultLifecycleObserver {
    private ShowMembersFragment view;
    public ShowMembersPresenter(ShowMembersFragment view){
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        //删除对框架类fragment的引用,系统才能成功回收资源
        view = null;
    }

    public void initData(MyRepository repository, int associationId) {
        repository.getAssociationMembers(associationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(members -> view.onInitSuccess(members),
                        throwable -> {
                            throwable.printStackTrace();
                        });
    }

    public void authorizeMember(int associationId, int adminId, MyRepository repository, int memberId) {
        repository.authorizeMember(associationId, adminId, memberId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(result -> {
                            if (result) {
                                view.onAuthorizeSuccess();
                            }
                        }
                        , throwable -> {
                            throwable.printStackTrace();
                            view.onAuthorizeFail();
                        });

    }
}
