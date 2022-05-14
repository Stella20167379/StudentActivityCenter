package com.example.graduatedesign.net;

import com.example.graduatedesign.data.model.NetResult;

import java.util.Map;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LoginService {

    @GET("free/login/code")
    Single<NetResult> getLoginVerifyCode(@Query("email") String email);

    @POST("free/login")
    Single<NetResult> login(@Body Map<String, Object> loginInfo);

    @POST("free/register")
    Single<NetResult> register(@Body Map data);

    @GET("free/validate/token")
    Single<NetResult> validateToken();

    @FormUrlEncoded
    @POST("free/validate/studentNo")
    Single<NetResult> validateStudentNo(@Field("collegeId") Integer collegeId, @Field("studentNo") String studentNo);

    @GET("free/register/code")
    Single<NetResult> getRegisterVerifyCode(@Query("email") String email);

    @FormUrlEncoded
    @POST("free/reset/pass")
    Single<NetResult> resetPass(@FieldMap Map<String,String> resetData);

    @GET("free/reset/code")
    Single<NetResult> getResetPassVerifyCode(@Query("email") String email);
}
