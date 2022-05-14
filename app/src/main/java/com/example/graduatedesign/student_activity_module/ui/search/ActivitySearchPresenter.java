package com.example.graduatedesign.student_activity_module.ui.search;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ActivitySearchPresenter implements DefaultLifecycleObserver {
    private ActivitySearchFragment view;

    public ActivitySearchPresenter(ActivitySearchFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        view = null;
    }

    public void searchActivities(MyRepository repository, String date, int schoolId) {
        repository.searchActivitiesByDate(date, schoolId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(activities -> {
                            if (activities == null || activities.size() < 1)
                                view.onSearchFail();
                            else view.onSearchSuccess(activities);
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            view.onSearchFail();
                        });
    }
}
