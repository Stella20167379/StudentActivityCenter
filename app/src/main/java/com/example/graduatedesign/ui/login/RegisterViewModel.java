package com.example.graduatedesign.ui.login;

import android.text.TextUtils;
import android.util.Patterns;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.graduatedesign.data.LoginRepository;
import com.example.graduatedesign.data.model.Result;
import com.example.graduatedesign.net.RetrofitExceptionResolver;

import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.internal.observers.ConsumerSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class RegisterViewModel extends ViewModel {
    private LoginRepository loginRepository;
    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<String> verifySendError = new MutableLiveData<>();
    private MutableLiveData<Result> registerResult = new MutableLiveData<>();

    @Inject
    public RegisterViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public MutableLiveData<String> getVerifySendError() {
        return verifySendError;
    }

    public MutableLiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    public MutableLiveData<Result> getRegisterResult() {
        return registerResult;
    }

    /**
     * 用户编辑完注册输入框内容后检验输入是否合法的回调
     *
     * @param email
     * @param pass
     * @param verifyCode
     */
    public void onRegisterDataChanged(String email, String nickname, String pass, String verifyCode) {
        RegisterFormState state = new RegisterFormState(false);

        if (!isEmailValid(email)) {
            state.setEmailError("请输入有效的邮箱地址");
        } else if (!isNickNameValid(nickname)) {
            state.setNicknameError("");
        } else if (!isPassValid(pass)) {
            state.setPassError("");
        } else if (verifyCode.trim().isEmpty() || TextUtils.isEmpty(verifyCode)) {
            state.setVerifyCodeError("验证码不能为空");
        } else {
            state.setValid(true);
        }
        registerFormState.setValue(state);
    }

    private boolean isEmailValid(String email) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }
        return !email.trim().isEmpty();
    }

    private boolean isPassValid(String pass) {
        if (pass.trim().isEmpty() || TextUtils.isEmpty(pass))
            return false;
        return pass.trim().length() > 5;
    }

    private boolean isNickNameValid(String nickname) {
        if (nickname.trim().isEmpty() || TextUtils.isEmpty(nickname))
            return false;
        return true;
    }

    public void getRegisterVerifyCode(String email) {
        loginRepository.getRegisterVerifyCode(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {},
                        throwable -> verifySendError.setValue(RetrofitExceptionResolver.resolve(throwable))
                );
    }

    public void register(Map<String, Object> registerData) {
        loginRepository.register(registerData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ConsumerSingleObserver<>(
                                result -> registerResult.setValue(result),
                                throwable -> registerResult.setValue(new Result.Error((Exception) throwable))
                        )
                );
    }

}
