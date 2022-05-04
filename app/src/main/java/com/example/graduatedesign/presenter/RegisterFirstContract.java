package com.example.graduatedesign.presenter;

import com.example.graduatedesign.data.model.Result;

import io.reactivex.rxjava3.core.Single;


public interface RegisterFirstContract {

    interface IRegisterFirstModel{
        Single<Result> validateStudentNo(int collegeId, String studentNo);
    }

    interface IRegisterFirstView{
        void onPassStudentNo();
        void onDenyStudentNo(String msg);
    }

    interface IRegisterFirstPresenter{
        void validateStudentNo(IRegisterFirstModel model, int collegeId, String studentNo);
    }

}
