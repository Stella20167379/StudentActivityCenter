package com.example.graduatedesign.presenter;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.association_module.data.Association;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.ui.home.HomeAssociationsFragment;

import java.util.List;

public class HomeAssociationsPresenter implements DefaultLifecycleObserver {
    private HomeAssociationsFragment view;

    public HomeAssociationsPresenter(HomeAssociationsFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        view=null;
    }

    /**
     * 网络查询获取显示总览所需的数据
     * @param schoolId 学校id
     * @param key 查询关键词
     */
    public void getShowAssociations(MyRepository repository,Integer schoolId,String key){
    }

}
