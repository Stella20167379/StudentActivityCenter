package com.example.graduatedesign.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.data.pagingsource.HomeActivitiesPagingSource;
import com.example.graduatedesign.net.RemoteApiService;
import com.example.graduatedesign.net.RetrofitExceptionResolver;

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
    public Flowable<PagingData<MyStudentActivity>> getActivities(Integer size){
        Pager<Integer,MyStudentActivity> pager=new Pager(
                //PagingConfig-分页配置
                new PagingConfig(size),
                //以函数代表pagingSourceFactory
                ()-> new HomeActivitiesPagingSource(retrofit.create(RemoteApiService.class))
        );

        return PagingRx.getFlowable(pager);
    }


    /**
     * 从网络获取未读消息
     * @param userId 接收人id
     * @return
     */
    public Single<Map<String,Object>> getMessageFromNet(Integer userId){
        return service.getMessages(userId)
                .map(netResult -> {
                    RetrofitExceptionResolver.analyzeNetResult(netResult);
                    Map<String,Object> data = (Map<String, Object>) netResult.getData();
                    return data;
                });
    }

    /**
     * 验证码给后端，后端查找对应的缓存在redis中的消息id，将其设为已读
     * @param code 服务器发送新消息时一起发过来的验证码
     * @return
     */
    public Completable notifyMsgRead(String code){
        return service.notifyMsgRead(code)
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
}
