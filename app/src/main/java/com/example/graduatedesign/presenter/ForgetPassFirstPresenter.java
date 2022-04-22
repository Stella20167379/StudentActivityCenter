package com.example.graduatedesign.presenter;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.LoginRepository;
import com.example.graduatedesign.net.RetrofitExceptionResolver;
import com.example.graduatedesign.ui.login.ForgetPassFirstFragment;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ForgetPassFirstPresenter implements DefaultLifecycleObserver {
    private ForgetPassFirstFragment view;

    public ForgetPassFirstPresenter(ForgetPassFirstFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        view=null;
    }

    public void getVerifyCode(String email, LoginRepository repository){
        repository.getResetPassVerifyCode(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(
                        () -> {},
                        throwable -> view.onGetVerifyCodeFail(RetrofitExceptionResolver.resolve(throwable))
                );
    }

    public void resetPass(Map<String,String> resetData, LoginRepository repository){
            repository.resetPass(resetData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .to(RxLifecycleUtils.bindLifecycle(view))
                    .subscribe(
                            () -> view.onSuccessReset(),
                            throwable -> view.onFailReset(RetrofitExceptionResolver.resolve(throwable))
                    );
    }
}
