package com.example.graduatedesign.presenter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.ui.home.HomeAssociationsFragment;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeAssociationsPresenter implements DefaultLifecycleObserver {
    private HomeAssociationsFragment view;
    private static final String TAG = "HomeAssociationsPresent";

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
     *
     * @param schoolId 学校id
     * @param key      查询关键词
     */
    public void getShowAssociations(MyRepository repository, Integer schoolId, String key) {
        repository.getAssociations(schoolId, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(associations -> {
                            if (associations.size() < 1)
                                view.onSearchFail();
                            else {
                                Log.d(TAG, "getShowAssociations数据长度:" + associations.size());
                                view.onSearchSuccess(associations);
                            }
                        }, throwable -> {
                            view.onSearchFail();
                            throwable.printStackTrace();
                        }
                );
    }

}
