package com.example.graduatedesign.net;

import com.example.graduatedesign.data.model.NetResult;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RemoteApiService {

    @GET("activity/page")
    Single<NetResult> getPageActivities(@Query("page") int page, @Query("size")int size,@Query("schoolId")Integer schoolId,@Query("key")String key);

    //后端传送发送人或接收人是userId的信息，并按时间降序排列
    @GET("message/all")
    Single<NetResult> getMessages(@Query("id")Integer userId);

    @PUT("message/notify/read")
    Single<NetResult> notifyMsgRead(@Query("senderId")int senderId,@Query("receiverId")int receiverId);


    /* 直接写GET请求会报错：Non-body HTTP method cannot contain @Body */
    //todo:目前没用到，所以没写
    @HTTP(method = "GET",path = "me",hasBody = true)
    Single<NetResult> checkNickname(@Body List<Integer> ids);

    @GET("bulletin/for/activity")
    Single<NetResult> getBulletinForActivity(@Query("activityId") int activityId);

    @GET("comment/for/activity")
    Single<NetResult> getCommentForActivity(@Query("activityId") int activityId);

    @GET("activity/for/user")
    Single<NetResult> searchMyActivities(@Query("userId") int userId);

    @GET("activity/filter/date")
    Single<NetResult> getActivitiesByDate(@Query("date") String date);

    @GET("association/all")
    Single<NetResult> getAssociations(@Query("schoolId") int schoolId);

    @GET("bulletin/for/association")
    Single<NetResult> getBulletinForAssociation(@Query("associationId") int associationId);

    @GET("association/for/user")
    Single<NetResult> getMyAssociations(@Query("userId") int userId);

    @GET("comment/for/user")
    Single<NetResult> getMyComments(@Query("userId") int userId);

    @GET("payRecord/for/user")
    Single<NetResult> getMyPayRecords(@Query("userId") int userId);

    @GET("association/get/members")
    Single<NetResult> getAssociationMembers(@Query("associationId") int associationId);

}
