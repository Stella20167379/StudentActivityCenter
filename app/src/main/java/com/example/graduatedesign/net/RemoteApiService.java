package com.example.graduatedesign.net;

import com.example.graduatedesign.data.model.NetResult;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface RemoteApiService {

    @GET("activity/page")
    Single<NetResult> getPageActivities(@Query("page") int page, @Query("size")int size,@Query("schoolId")Integer schoolId,@Query("key")String key);

    //todo:以下这一段待做
    //后端传送发送人或接收人是userId的信息，并按时间降序排列
    @GET("message/all")
    Single<NetResult> getMessages(@Query("id") Integer userId);

    @PUT("message/notify/read")
    Single<NetResult> notifyMsgRead(@Query("senderId") int senderId, @Query("receiverId") int receiverId);

    /* 直接写GET请求会报错：Non-body HTTP method cannot contain @Body */
    @HTTP(method = "GET", path = "me", hasBody = true)
    Single<NetResult> checkNickname(@Body List<Integer> ids);

    @GET("bulletin/all")
    Single<NetResult> getBulletins(@Query("type") int type, @Query("ownerId") int ownerId);

    @GET("comment/for/activity")
    Single<NetResult> getCommentsForActivity(@Query("activityId") int activityId, @Query("userId") int userId);

    @GET("activity/for/user")
    Single<NetResult> getActivitiesForUser(@Query("userId") int userId);

    @GET("activity/filter/date")
    Single<NetResult> searchActivitiesByDate(@Query("date") String date, @Query("schoolId") int schoolId);

    @GET("association/all")
    Single<NetResult> getAssociations(@Query("schoolId") int schoolId, @Query("key") String key);

    @GET("association/for/user")
    Single<NetResult> getAssociationsForUser(@Query("userId") int userId);

    @GET("comment/for/user")
    Single<NetResult> getCommentsForUser(@Query("userId") int userId);

    @GET("payRecord/for/user")
    Single<NetResult> getPayRecordsForUser(@Query("userId") int userId);

    @GET("user/detail")
    Single<NetResult> getPersonalDetailForUser(@Query("userId") int userId);

    @GET("user/association/members")
    Single<NetResult> getAssociationMembers(@Query("associationId") int associationId);

    @GET("association/show/detail")
    Single<NetResult> getAssociationDetailShowData(@Query("userId") int userId, @Query("associationId") int associationId);

    @POST("association/edit")
    Single<NetResult> saveAssociationEditWithFile(@Body RequestBody body);

    @PUT("association/authorize")
    Single<NetResult> authorizeMember(@Query("associationId") int associationId, @Query("adminId") int adminId, @Query("memberId") int memberId);

    @POST("user/personal/edit")
    Single<NetResult> savePersonalEditWithFile(@Body RequestBody body);

    @POST("activity/add")
    Single<NetResult> addActivity(@Body RequestBody body);

    @PUT("comment/add")
    Single<NetResult> addComment(@Query("commentJson") String commentJson);

    @GET("activity/show/detail")
    Single<NetResult> getActivityDetailShowData(@Query("userId") int userId, @Query("activityId") int activityId);

    @GET("association/tag/admin")
    Single<NetResult> selectAdminAssociations(@Query("adminId") int adminId);

    @PUT("activity/sign")
    Single<NetResult> signToActivity(@Query("userId") int userId, @Query("activityId") int activityId);

    @Headers("Content-Type:image/png; charset=utf-8")
    @GET("alipay/qrcode/io")
    Single<ResponseBody> getAlipayQrCode(@Query("userId") int userId, @Query("activityId") int activityId);

    @GET("alipay/qrcode")
    Single<NetResult> getAlipayQrCodePath(@Query("userId") int userId, @Query("activityId") int activityId);

    @GET("alipay/sign")
    Single<NetResult> getSignedAlipayInfo(@Query("userId") int userId, @Query("activityId") int activityId);

    @PUT("activity/check/ioo")
    Single<NetResult> checkInOrOut(@Query("userId") int userId, @Query("activityId") int activityId);

    @GET("free/logout")
    Single<NetResult> logOut();
}
