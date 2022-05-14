package com.example.graduatedesign.presenter;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.model.Result;
import com.example.graduatedesign.net.RetrofitExceptionResolver;
import com.example.graduatedesign.ui.login.RegisterFirstFragment;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.internal.observers.ConsumerSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterFirstPresenter implements RegisterFirstContract.IRegisterFirstPresenter {
    private RegisterFirstFragment view;

    public RegisterFirstPresenter(RegisterFirstFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        /* 接口继承 */
        RegisterFirstContract.IRegisterFirstPresenter.super.onDestroy(owner);
        view = null;
    }

    @Override
    public void validateStudentNo(RegisterFirstContract.IRegisterFirstModel model,int collegeId,String studentNo){
        model.validateStudentNo(collegeId, studentNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(new ConsumerSingleObserver<>(
                                result -> {
                                    if (result instanceof Result.Success) {
                                        Double raw = (Double) ((Result.Success<?>) result).getData();
                                        view.onPassStudentNo(raw.intValue());
                                    } else
                                        view.onDenyStudentNo(result.toString());
                                },
                                throwable -> view.onDenyStudentNo(RetrofitExceptionResolver.resolve(throwable))
                        )
                );
    }

}
