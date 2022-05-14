package com.example.graduatedesign.ui.login;

import android.util.Patterns;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.graduatedesign.R;
import com.example.graduatedesign.data.LoginRepository;
import com.example.graduatedesign.data.model.Result;
import com.example.graduatedesign.net.RetrofitExceptionResolver;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.internal.observers.ConsumerSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class LoginViewModel extends ViewModel {
    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<Result> loginResult = new MutableLiveData<>();
    private Integer loginType = 1;
    private MutableLiveData<String> verifyCodeError = new MutableLiveData<>();
    private final LoginRepository loginRepository;

    @Inject
    public LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public MutableLiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public MutableLiveData<Result> getLoginResult() {
        return loginResult;
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    public MutableLiveData<String> getVerifyCodeError() {
        return verifyCodeError;
    }

    /**
     * 登录输入框内容变化后，初步检验是否合法
     */
    public void onLoginFormChanged(String principle, String credential, String verifyCode) {
        LoginFormState state = new LoginFormState(false);
        if (loginType == 1) {
            if (!isPrincipleValid(principle))
                state.setPrincipalError(R.string.error_invalid_principle);
            else if (!isCredentialValid(credential))
                state.setCredentialError(R.string.error_invalid_credential);
            else state.setDataValid(true);
        } else {
            if (!isPrincipleValid(principle))
                state.setPrincipalError(R.string.error_invalid_email);
            else if (!isCredentialValid(verifyCode))
                state.setCredentialError(R.string.error_invalid_verify_code);
            else state.setDataValid(true);
        }
        loginFormState.setValue(state);
    }

    // A placeholder principle validation check
    private boolean isPrincipleValid(String principle) {
        if (principle == null) {
            return false;
        }
        if (principle.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(principle).matches();
        } else {
            return !principle.trim().isEmpty();
        }
    }

    // A placeholder credential validation check
    private boolean isCredentialValid(String credential) {
        return credential != null && credential.trim().length() > 5;
    }

    /**
     * 登录验证
     *
     * @param principle  登录账号，此为邮箱
     * @param credential 登录密码/验证码
     */
    public void login(String principle, String credential) {
        loginRepository.login(principle, credential, loginType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ConsumerSingleObserver<>(
                                result -> loginResult.setValue(result),
                                throwable -> {
                                    loginResult.setValue(new Result.Error((Exception) throwable));
                                }
                        )
                );

    }

    /**
     * 验证码登录时请求发送验证码邮件
     *
     * @param email 邮箱
     */
    public void getLoginVerifyCode(String email) {
        loginRepository.getLoginVerifyCode(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                        },
                        throwable -> verifyCodeError.setValue(RetrofitExceptionResolver.resolve(throwable))
                );
    }


}
