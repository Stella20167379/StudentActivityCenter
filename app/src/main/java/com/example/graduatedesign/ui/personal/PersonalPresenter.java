package com.example.graduatedesign.ui.personal;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PersonalPresenter implements DefaultLifecycleObserver {
    private static final String TAG = "PersonalPresenter";
    private PersonalFragment view;

    public PersonalPresenter(PersonalFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        view = null;
    }

    public void logout(MyRepository repository) {
        repository.logOut()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(result -> {
                            if (result)
                                view.onLogOutSuccess();
                        }, throwable -> {
                            view.onLogOutSuccess();
                            throwable.printStackTrace();
                        }
                );
    }

}
