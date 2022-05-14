package com.example.graduatedesign.data.remote;

import androidx.annotation.NonNull;

import com.example.graduatedesign.data.model.NetResult;
import com.example.graduatedesign.net.LoginService;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Retrofit;

/**
 * 负责返回可进行网络访问工作的 rxjava对象
 */
@Singleton
public class RemoteLoginDataSource {
    private final Retrofit retrofit;
    private final LoginService service;

    @Inject
    public RemoteLoginDataSource(@NonNull Retrofit retrofit) {
        this.retrofit = retrofit;
        service=retrofit.create(LoginService.class);
    }

    public Single<NetResult> login(Map<String, Object> loginInfo){
        return retrofit.create(LoginService.class)
                .login(loginInfo);
    }

    public Single<NetResult> getLoginVerifyCode(String email){
        return service
                .getLoginVerifyCode(email);
    }

    public Single<NetResult> validateToken(String tokenName,String token){
        return retrofit.create(LoginService.class)
                .validateToken();
    }

    public Single<NetResult> validateStudentNo(Integer collegeId,String studentNo){
        return retrofit.create(LoginService.class)
                .validateStudentNo(collegeId,studentNo);
    }

    public Single<NetResult> getRegisterVerifyCode(String email){
        return service
                .getRegisterVerifyCode(email);
    }

    public Single<NetResult> register(Map<String,Object> registerData){
        return retrofit.create(LoginService.class)
                .register(registerData);
    }

    public Single<NetResult> resetPass(Map<String,String> resetData){
        return retrofit.create(LoginService.class)
                .resetPass(resetData);
    }

    public Single<NetResult> getResetPassVerifyCode(String email){
        return service
                .getResetPassVerifyCode(email);
    }

}
