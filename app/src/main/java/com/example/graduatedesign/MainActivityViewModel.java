package com.example.graduatedesign;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.graduatedesign.data.LoginRepository;
import com.example.graduatedesign.data.model.Result;
import com.example.graduatedesign.net.RetrofitExceptionResolver;
import com.example.graduatedesign.personal_module.data.User;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MainActivityViewModel extends ViewModel {

    private static final String TAG = "MainActivityViewModel";
    private String loginPrompt;
    private final LoginRepository loginRepository;
    /* 控制登录状态、页面跳转的参数，null-未登录，其余-登录 请勿随意修改 */
    private final MutableLiveData<User> userInfo = new MutableLiveData<>();
    private final MutableLiveData<Boolean> tokenState=new MutableLiveData<>();

    @Inject
    public MainActivityViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public MutableLiveData<User> getUserInfo() {
        return userInfo;
    }

    public MutableLiveData<Boolean> getTokenState() {
        return tokenState;
    }

    /**
     * 用于启动应用时，自动检查用户的登录状态
     * 对不同的登录状态，通过改变 userInfo 的值手动触发事件跳转页面
     *
     */
    public void checkLoginState(String tokenName,String token) {
        if (token != null) {
            loginRepository.validateToken(tokenName,token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            //连接成功且服务器返回符合要求的数据时
                            result -> {
                                if (result instanceof Result.Success) {
                                    tokenState.setValue(true);
                                } else {
                                    userInfo.setValue(null);
                                }
                            },
                            //发生错误时，例如网络连接错误
                            throwable -> {
                                loginPrompt=RetrofitExceptionResolver.resolve(throwable);
                                userInfo.setValue( null);
                            }
                    );
        } else {
            loginPrompt = null;
            userInfo.setValue(null);
        }
    }

    public String getLoginPrompt() {
        return loginPrompt;
    }

    public void setLoginPrompt(String loginPrompt) {
        this.loginPrompt = loginPrompt;
    }
}
