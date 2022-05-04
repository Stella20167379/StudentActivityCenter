package com.example.graduatedesign.ui.bulletin;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;

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
     * @param type 公告类型，本系统分为活动公告和社团公告
     * @param ownerId 所属活动/社团id
     */
    public void initData(int type,MyRepository repository, int ownerId){

    }
}
