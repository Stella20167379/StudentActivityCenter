package com.example.graduatedesign.presenter;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.LoginRepository;
import com.example.graduatedesign.data.model.Result;
import com.example.graduatedesign.net.RetrofitExceptionResolver;
import com.example.graduatedesign.ui.login.RegisterFirstFragment;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.internal.observers.ConsumerSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterFirstPresenter implements DefaultLifecycleObserver {
    private RegisterFirstFragment view;

    public RegisterFirstPresenter(RegisterFirstFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        //删除对框架类fragment的引用,系统才能成功回收资源
        view=null;
    }

    public void validateStudentNo(LoginRepository repository,Integer collegeId,String studentNo){
        repository.validateStudentNo(collegeId,studentNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(new ConsumerSingleObserver<>(
                                result -> {
                                    if (result instanceof Result.Success)
                                        view.onPassStudentNo();
                                    else
                                        view.onDenyStudentNo(result.toString());
                                },
                                throwable -> view.onDenyStudentNo(RetrofitExceptionResolver.resolve(throwable))
                        )
                );
    }
    
}
