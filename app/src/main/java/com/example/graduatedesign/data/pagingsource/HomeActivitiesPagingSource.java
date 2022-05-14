package com.example.graduatedesign.data.pagingsource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.data.model.NetResult;
import com.example.graduatedesign.net.RemoteApiService;
import com.example.graduatedesign.utils.GsonConvertTypes;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeActivitiesPagingSource extends RxPagingSource<Integer, MyStudentActivity> {
    private static final String TAG = "HomeActivitiesPagingSou";
    private final RemoteApiService service;
    private final Map params = ActivityPagingSourceParams.getParams();

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
        Integer page = loadParams.getKey();
        if (page == null)
            page = 1;
        Integer size = loadParams.getLoadSize();
        Integer schoolId = (Integer) params.get("schoolId");
        String key = (String) params.get("key");
        return service.getPageActivities(page, size, schoolId, key)
                .subscribeOn(Schedulers.io())
                .map(this::toLoadResult)
                .doOnError(throwable -> throwable.printStackTrace())
                .onErrorReturn(LoadResult.Error::new);
    }

    /**
     * 将分页的List数据包裹成Page对象，保存分页状态
     */
    private LoadResult<Integer, MyStudentActivity> toLoadResult(@NonNull NetResult netResult) {
        Map<String, Object> data = (Map<String, Object>) netResult.getData();
//        Integer prev = (Integer) data.get("prev");
        Object rawNext = data.get("next");
        Integer next;
        if (rawNext == null)
            next = null;
        else next = (Integer) rawNext;
//        Log.d(TAG, "toLoadResult,prev: "+prev);
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(data.get("items"));
        List<MyStudentActivity> items = gson.fromJson(jsonElement, GsonConvertTypes.gsonTypeOfActivityList);
        return new LoadResult.Page(items, null, next, LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED);
    }
}
