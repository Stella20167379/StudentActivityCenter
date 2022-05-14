package com.example.graduatedesign.presenter;

import androidx.lifecycle.DefaultLifecycleObserver;

import com.example.graduatedesign.data.model.Result;

import io.reactivex.rxjava3.core.Single;


public interface RegisterFirstContract {

    interface IRegisterFirstModel {
        Single<Result> validateStudentNo(int collegeId, String studentNo);
    }

    interface IRegisterFirstView {
        void onPassStudentNo(int credentialInfoId);

        void onDenyStudentNo(String msg);
    }

    interface IRegisterFirstPresenter extends DefaultLifecycleObserver {
        void validateStudentNo(IRegisterFirstModel model, int collegeId, String studentNo);

    }

}
