package com.example.graduatedesign.ui.login;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.graduatedesign.R;
import com.example.graduatedesign.data.LoginRepository;
import com.example.graduatedesign.data.model.Result;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ForgetPassViewModel extends ViewModel {
    private LoginRepository loginRepository;
    private MutableLiveData<ForgetPassFormState> formState=new MutableLiveData<>();
    private MutableLiveData<Result> resetPassResult=new MutableLiveData<>();

    @Inject
    public ForgetPassViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public MutableLiveData<ForgetPassFormState> getFormState() {
        return formState;
    }

    public void onFormDataChanged(String old1,String new1,String confirm1){
        ForgetPassFormState state=new ForgetPassFormState(false);
        if (!isPassValid(old1))
            state.setOld1Error(R.string.error_pass);
        else if (!isPassValid(new1))
            state.setNew1Error(R.string.error_pass);
        else if (!isPassValid(confirm1))
            state.setConfirmError(R.string.error_pass);
        else if (!new1.equals(confirm1))
            state.setConfirmError(R.string.error_confirm_pass);
        else
            state.setValid(true);
    }

    private boolean isPassValid(String pass) {
        if (pass.trim().isEmpty() || TextUtils.isEmpty(pass))
            return false;
        return pass.trim().length() > 5;
    }

    public void resetPass(String old1,String new1){}

    public MutableLiveData<Result> getResetPassResult() {
        return resetPassResult;
    }
}
