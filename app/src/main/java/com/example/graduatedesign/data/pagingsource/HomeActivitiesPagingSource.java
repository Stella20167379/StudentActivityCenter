package com.example.graduatedesign.data.pagingsource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.data.model.NetResult;
import com.example.graduatedesign.net.RemoteApiService;
import com.example.graduatedesign.utils.DataUtil;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeActivitiesPagingSource extends RxPagingSource<Integer, MyStudentActivity> {
    private final RemoteApiService service;

    public HomeActivitiesPagingSource(@NonNull RemoteApiService service) {
        this.service = service;
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, MyStudentActivity> pagingState) {
        return null;
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, MyStudentActivity>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        //首次查询时，默认为第一页
        Integer page=loadParams.getKey();
        if (page==null)
            page=1;
        Integer size = loadParams.getLoadSize();
        return service.getPageActivities(page,size)
                .subscribeOn(Schedulers.io())
                .map(this::toLoadResult)
                .onErrorReturn(LoadResult.Error::new);
    }

    /**
     * 将分页的List数据包裹成Page对象，保存分页状态
     */
    private LoadResult<Integer, MyStudentActivity> toLoadResult(@NonNull NetResult netResult) {
        Map<String,Object> data= (Map<String, Object>) netResult.getData();
        Integer prev= DataUtil.getIntFromGsonMap(data,"prev");
        Integer next=DataUtil.getIntFromGsonMap(data,"next");
        List<MyStudentActivity> items= (List<MyStudentActivity>) data.get("items");

        return new LoadResult.Page<>(items, prev, next);

    }
}
