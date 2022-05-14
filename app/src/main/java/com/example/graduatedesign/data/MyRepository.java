package com.example.graduatedesign.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.example.graduatedesign.association_module.data.Association;
import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.data.pagingsource.HomeActivitiesPagingSource;
import com.example.graduatedesign.net.RemoteApiService;
import com.example.graduatedesign.net.RetrofitExceptionResolver;
import com.example.graduatedesign.personal_module.data.PayRecord;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.student_activity_module.data.Comment;
import com.example.graduatedesign.ui.bulletin.data.SimpleBulletin;
import com.example.graduatedesign.utils.GsonConvertTypes;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

@Singleton
public class MyRepository {
    private final Retrofit retrofit;
    private final RemoteApiService service;
    private final Gson gson;

    @Inject
    public MyRepository(@NonNull Retrofit retrofit, Gson gson) {
        this.retrofit = retrofit;
        this.gson = gson;
        service = retrofit.create(RemoteApiService.class);
    }

    /**
     * 配置分页，返回分页数据源流
     *
     * @param size 分页大小
     */
    public Flowable<PagingData<MyStudentActivity>> getActivities(Integer size) {
        Pager<Integer, MyStudentActivity> pager = new Pager(
                //PagingConfig-分页配置
                new PagingConfig(size),
                //以函数代表pagingSourceFactory
                () -> new HomeActivitiesPagingSource(retrofit.create(RemoteApiService.class))
        );

        return PagingRx.getFlowable(pager);
    }

    /**
     * 从网络获取消息
     *
     * @param userId 接收人id
     */
    public Single<List<Message>> getMessagesFromNet(Integer userId) {
        return null;
    }

    /**
     * 验证码给后端，后端查找对应的缓存在redis中的消息id，将其设为已读
     */
    public Completable notifyMsgRead(int senderId, int receiverId) {
        return service.notifyMsgRead(senderId, receiverId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    return null;
                })
                .ignoreElement();
    }

    /**
     * 用于获取指定id的最新昵称
     *
     * @param ids 用户id列表
     */
    public Single<Map<Integer, String>> checkNickname(List<Integer> ids) {
        return service.checkNickname(ids)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    Map<Integer, String> newNickname = (Map<Integer, String>) netResult.getData();
                    return newNickname;
                });
    }

    /**
     * 获取活动相关的公告
     *
     * @param type    公告类型，本系统分为活动公告和社团公告
     * @param ownerId 所属活动/社团id
     */
    public Single<List<SimpleBulletin>> getBulletins(int type, int ownerId) {
        return service.getBulletins(type, ownerId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    JsonElement jsonElement = gson.toJsonTree(netResult.getData());
                    return gson.fromJson(jsonElement, GsonConvertTypes.gsonTypeOfBulletinList);
                });
    }

    /**
     * 获取活动相关的评论
     *
     * @param activityId 活动id
     * @param userId     当前用户id，用以查询对当前活动，当前用户是否已经发布过评论
     */
    public Single<Map<String, Object>> getCommentsForActivity(int activityId, int userId) {
        return service.getCommentsForActivity(activityId, userId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    //将服务器返回的列表转换为pojo列表
                    Map<String, Object> data = (Map<String, Object>) netResult.getData();
                    List dataList = (List) data.get("list");
                    JsonElement jsonElement = gson.toJsonTree(dataList);
                    List<Comment> commentList = gson.fromJson(jsonElement, GsonConvertTypes.gsonTypeOfCommentList);
                    //当前用户是否发布过评论
                    boolean isCommented = (boolean) data.get("isCommented");
                    //返回的最终结果
                    Map<String, Object> result = new ArrayMap<>();
                    result.put("isCommented", isCommented);
                    result.put("list", commentList);
                    //用户发布的评论内容
                    if (isCommented) {
                        String content = (String) data.get("content");
                        double score = (double) data.get("score");
                        result.put("content", content);
                        result.put("score", score);
                    }
                    return result;
                });
    }

    /**
     * 根据学校查找相关社团
     *
     * @param schoolId 学校id
     * @param key      搜索的关键词，为空时代表搜索所有
     */
    public Single<List<Association>> getAssociations(int schoolId, String key) {
        return service.getAssociations(schoolId, key)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    JsonElement jsonElement = gson.toJsonTree(netResult.getData());
                    return gson.fromJson(jsonElement, GsonConvertTypes.gsonTypeOfAssociationList);
                });
    }

    /**
     * 查找用户参与的活动列表
     *
     * @param userId 用户id
     */
    public Single<List<MyStudentActivity>> getActivitiesForUser(int userId) {
        return service.getActivitiesForUser(userId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    JsonElement jsonElement = gson.toJsonTree(netResult.getData());
                    return gson.fromJson(jsonElement, GsonConvertTypes.gsonTypeOfActivityList);
                });
    }

    /**
     * 查找用户参与的社团
     *
     * @param userId 用户id
     */
    public Single<List<Association>> getAssociationsForUser(int userId) {
        return service.getAssociationsForUser(userId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    JsonElement jsonElement = gson.toJsonTree(netResult.getData());
                    return gson.fromJson(jsonElement, GsonConvertTypes.gsonTypeOfAssociationList);
                });
    }

    /**
     * 查找用户发出的评论
     *
     * @param userId 用户id
     */
    public Single<List<Comment>> getCommentsForUser(int userId) {
        return service.getCommentsForUser(userId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    JsonElement jsonElement = gson.toJsonTree(netResult.getData());
                    return gson.fromJson(jsonElement, GsonConvertTypes.gsonTypeOfCommentList);
                });
    }

    /**
     * 查找用户付款记录
     *
     * @param userId 用户id
     */
    public Single<List<PayRecord>> getPayRecordsForUser(int userId) {
        return service.getPayRecordsForUser(userId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    JsonElement jsonElement = gson.toJsonTree(netResult.getData());
                    return gson.fromJson(jsonElement, GsonConvertTypes.gsonTypeOfPayRecordList);
                });
    }

    /**
     * 查看社团成员
     *
     * @param associationId 社团id
     */
    public Single<List<User>> getAssociationMembers(int associationId) {
        return service.getAssociationMembers(associationId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    JsonElement jsonElement = gson.toJsonTree(netResult.getData());
                    return gson.fromJson(jsonElement, GsonConvertTypes.gsonTypeOfUserList);
                });
    }

    /**
     * 用户打开社团详情页面，查询 当前要展示的社团详情对象 + 该社团最新一条公告 + 该社团关联的活动 + 当前用户跟当前社团的关系
     *
     * @param userId        用户id
     * @param associationId 社团id
     * @return
     */
    public Single<Map<String, Object>> getAssociationDetailShowData(int userId, int associationId) {
        return service.getAssociationDetailShowData(userId, associationId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    Map<String, Object> data = (Map<String, Object>) netResult.getData();
                    if (data == null)
                        throw new IllegalStateException("没有查询到数据！");
                    return data;
                });
    }

    /**
     * 管理员修改社团信息时，修改了头像，不论是否修改其余参数，都需要调用此方法
     *
     * @param body 包含文件的请求体
     */
    public Single<String> saveAssociationEditWithFile(RequestBody body) {
        return service.saveAssociationEditWithFile(body)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    String imgPath = (String) netResult.getData();
                    return imgPath;
                });
    }

    /**
     * 授权社团成员为管理员
     *
     * @param associationId 社团id
     * @param adminId       当前用户id
     * @param memberId      授权目标id
     * @return 返回true，或抛异常
     */
    public Single<Boolean> authorizeMember(int associationId, int adminId, int memberId) {
        return service.authorizeMember(associationId, adminId, memberId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    return true;
                });
    }

    /**
     * 用户通过日期筛选该天未结束的活动
     *
     * @param date     日期
     * @param schoolId 学校id
     */
    public Single<List<MyStudentActivity>> searchActivitiesByDate(String date, int schoolId) {
        return service.searchActivitiesByDate(date, schoolId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    JsonElement jsonElement = gson.toJsonTree(netResult.getData());
                    return gson.fromJson(jsonElement, GsonConvertTypes.gsonTypeOfActivityList);
                });
    }

    /**
     * 用户修改个人信息
     *
     * @param body 包含文件的请求体
     */
    public Single<List<String>> savePersonalEditWithFile(RequestBody body) {
        return service.savePersonalEditWithFile(body)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    if (netResult.getData() != null) {
                        List<String> data = (List<String>) netResult.getData();
                        return data;
                    }
                    return new ArrayList<>();
                });
    }

    /**
     * 用户打开个人详情编辑界面，查询个人相关详情信息
     *
     * @param userId 用户id
     */
    public Single<User> getPersonalDetailForUser(int userId) {
        return service.getPersonalDetailForUser(userId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    JsonElement jsonElement = gson.toJsonTree(netResult.getData());
                    User userDetail = gson.fromJson(jsonElement, User.class);
                    return userDetail;
                });
    }

    /**
     * 管理员添加社团
     *
     * @param body 包含文件的请求体
     */
    public Single<Boolean> addActivity(RequestBody body) {
        return service.addActivity(body)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    return true;
                });
    }

    /**
     * 用户对活动进行评论
     *
     * @param commentJson comment对象转化为的json形式字符串
     */
    public Single<List<Comment>> addComment(String commentJson) {
        return service.addComment(commentJson)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    JsonElement jsonElement = gson.toJsonTree(netResult.getData());
                    return gson.fromJson(jsonElement, GsonConvertTypes.gsonTypeOfCommentList);
                });
    }

    /**
     * 用户打开活动详情页面，查询 当前要展示的活动详情对象（包括用户与活动的关系） + 该社团最新一条公告
     *
     * @param activityId 活动id
     * @param userId     用户id
     */
    public Single<Map<String, Object>> getActivityDetailShowData(int userId, int activityId) {
        return service.getActivityDetailShowData(userId, activityId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    Map<String, Object> data = (Map<String, Object>) netResult.getData();
                    if (data == null)
                        throw new IllegalStateException("没有查询到数据！");
                    return data;
                });
    }

    /**
     * 查找社团管理员管理的社团，需要id+社团名称
     *
     * @param userId 用户id
     */
    public Single<List<Association>> selectAdminAssociations(int userId) {
        return service.selectAdminAssociations(userId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    if (netResult.getData() == null)
                        return null;
                    JsonElement jsonElement = gson.toJsonTree(netResult.getData());
                    return gson.fromJson(jsonElement, GsonConvertTypes.gsonTypeOfAssociationList);
                });
    }

    /**
     * 用户报名活动
     *
     * @param userId     用户id
     * @param activityId 活动id
     */
    public Single<Boolean> signToActivity(int userId, int activityId) {
        return service.signToActivity(userId, activityId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    return true;
                });
    }

    /**
     * 用户报名付费活动，且已安装支付宝沙箱，向服务器请求生成订单信息
     *
     * @param userId     用户id
     * @param activityId 活动id
     * @return 签名后的订单信息，用以拉起支付宝支付
     */
    public Single<String> getSignedAlipayInfo(int userId, int activityId) {
        return service.getSignedAlipayInfo(userId, activityId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    String orderInfo = (String) netResult.getData();
                    return orderInfo;
                });
    }

    /**
     * 用户报名付费活动，但未安装支付宝沙箱，向服务器请求生成收款二维码
     * 注意：图片转换操作需在此处，即presenter进行subscribe操作前，实现异步转换，然后在主线程提交视图控件进行显示
     *
     * @param userId     用户id
     * @param activityId 活动id
     * @return 由字节数组转换后的二维码比特图
     */
    @Deprecated
    public Single<Bitmap> getAlipayQrCodeBitMap(int userId, int activityId) {
        return service.getAlipayQrCode(userId, activityId)
                .map(body -> body.byteStream())
                .map(is -> BitmapFactory.decodeStream(is))
                ;
    }

    public Single<String> getAlipayQrCodePath(int userId, int activityId) {
        return service.getAlipayQrCodePath(userId, activityId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    return netResult.getMsg();
                })
                ;
    }

    /**
     * 用户签到或者签退,后端若操作成功需返回新的用户状态
     *
     * @param userId     用户id
     * @param activityId 活动id
     */
    public Single<Integer> checkInOrOut(int userId, int activityId) {
        return service.checkInOrOut(userId, activityId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    Double state = (Double) netResult.getData();
                    return state.intValue();
                });
    }

    /**
     * 注销登录
     */
    public Single<Boolean> logOut() {
        return service.logOut()
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    return true;
                });
    }

}
