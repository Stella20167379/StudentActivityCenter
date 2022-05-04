package com.example.graduatedesign.data;

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
import com.example.graduatedesign.student_activity_module.data.Bulletin;
import com.example.graduatedesign.student_activity_module.data.Comment;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Retrofit;

@Singleton
public class MyRepository{
    private final Retrofit retrofit;
    private final RemoteApiService service;

    @Inject
    public MyRepository(@NonNull Retrofit retrofit) {
        this.retrofit = retrofit;
        service=retrofit.create(RemoteApiService.class);
    }

    /**
     * 配置分页，返回分页数据源流
     * @param size 分页大小
     * @return
     */
    public Flowable<PagingData<MyStudentActivity>> getActivities(Integer size,Integer schoolId,String key){
        Pager<Integer,MyStudentActivity> pager=new Pager(
                //PagingConfig-分页配置
                new PagingConfig(size),
                //以函数代表pagingSourceFactory
                ()-> new HomeActivitiesPagingSource(retrofit.create(RemoteApiService.class), schoolId, key)
        );

        return PagingRx.getFlowable(pager);
    }

    /**
     * 从网络获取消息
     * @param userId 接收人id
     * @return
     */
    public Single<List<Message>> getMessageFromNet(Integer userId){
        return service.getMessages(userId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    List<Message> data = (List<Message>) netResult.getData();
                    return data;
                });
    }

    /**
     * 验证码给后端，后端查找对应的缓存在redis中的消息id，将其设为已读
     * @return
     */
    public Completable notifyMsgRead(int senderId,int receiverId){
        return service.notifyMsgRead(senderId,receiverId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    return null;
                })
                .ignoreElement();
    }

    /**
     * 用于获取指定id的最新昵称
     * @param ids 用户id列表
     * @return
     */
    public Single<Map<Integer,String>> checkNickname(List<Integer> ids){
        return service.checkNickname(ids)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    Map<Integer,String> newNickname= (Map<Integer, String>) netResult.getData();
                    return newNickname;
                });
    }

    /**
     * 获取活动相关的公告
     * @param activityId 活动id
     * @return
     */
    public Single<List<Bulletin>> getBulletinForActivity(int activityId){
        return service.getBulletinForActivity(activityId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    List<Bulletin> bulletins= (List<Bulletin>) netResult.getData();
                    return bulletins;
                });
    }

    /**
     * 获取活动相关的评论
     * @param activityId 活动id
     * @return
     */
    public Single<List<Comment>> getCommentForActivity(int activityId){
        return service.getCommentForActivity(activityId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    List<Comment> commentList= (List<Comment>) netResult.getData();
                    return commentList;
                });
    }

    /**
     * 查找用户参与的活动列表
     * @param userId 用户id
     * @return
     */
    public Single<List<MyStudentActivity>> searchMyActivities(int userId){
        return service.searchMyActivities(userId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    List<MyStudentActivity> activityList= (List<MyStudentActivity>) netResult.getData();
                    return activityList;
                });
    }

    /**
     * 根据学校查找相关社团
     * @param schoolId 学校id
     * @return
     */
    public Single<List<Association>> getAssociations(int schoolId){
        return service.getAssociations(schoolId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    List<Association> activityList= (List<Association>) netResult.getData();
                    return activityList;
                });
    }

    /**
     * 获取社团相关的公告
     * @param associationId 社团id
     * @return
     */
    public Single<List<Bulletin>> getBulletinForAssociation(int associationId){
        return service.getBulletinForAssociation(associationId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    List<Bulletin> bulletins= (List<Bulletin>) netResult.getData();
                    return bulletins;
                });
    }

    /**
     * 查找用户参与的社团
     * @param userId 用户id
     * @return
     */
    public Single<List<Association>> getMyAssociations(int userId){
        return service.getMyAssociations(userId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    List<Association> bulletins= (List<Association>) netResult.getData();
                    return bulletins;
                });
    }

    /**
     * 查找用户发出的评论
     * @param userId 用户id
     * @return
     */
    public Single<List<Comment>> getMyComments(int userId){
        return service.getMyComments(userId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    List<Comment> bulletins= (List<Comment>) netResult.getData();
                    return bulletins;
                });
    }

    /**
     * 查找用户付款记录
     * @param userId 用户id
     * @return
     */
    public Single<List<PayRecord>> getMyPayRecords(int userId){
        return service.getMyPayRecords(userId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    List<PayRecord> bulletins= (List<PayRecord>) netResult.getData();
                    return bulletins;
                });
    }

    /**
     * 查看社团成员
     * @param associationId 社团id
     * @return
     */
    public Single<List<User>> getAssociationMembers(int associationId){
        return service.getAssociationMembers(associationId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    List<User> bulletins= (List<User>) netResult.getData();
                    return bulletins;
                });
    }

}
