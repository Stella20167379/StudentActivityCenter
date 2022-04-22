package com.example.graduatedesign.net;

import com.example.graduatedesign.data.model.NetResult;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RemoteApiService {

    @GET("activity/page")
    Single<NetResult> getPageActivities(@Query("page") int page, @Query("size")int size);

    @GET("message/all")
    Single<NetResult> getMessages(@Query("id")Integer userId);

    @GET("message/notify/read")
    Single<NetResult> notifyMsgRead(@Query("code")String code);

    @GET("message/check/nickname")
    Single<NetResult> checkNickname(@Body List<Integer> ids);

}
