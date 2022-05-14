package com.example.graduatedesign.ui.bulletin;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BulletinDetailPresenter implements DefaultLifecycleObserver {
    private BulletinDetailFragment view;

    public BulletinDetailPresenter(BulletinDetailFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        //删除对框架类fragment的引用,系统才能成功回收资源
        view=null;
    }

    /**
     * 查看所有公告页面的初始化数据方法
     *
     * @param type    公告类型，本系统分为活动公告和社团公告
     * @param ownerId 所属活动/社团id
     */
    public void initData(int type, MyRepository repository, int ownerId) {
        repository.getBulletins(type, ownerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(bulletins -> {
                            if (bulletins.size() < 1)
                                view.onInitFail();
                            else view.onInitSuccess(bulletins);
                        }, throwable -> {
                            view.onInitFail();
                            throwable.printStackTrace();
                        }
                );
    }
}
