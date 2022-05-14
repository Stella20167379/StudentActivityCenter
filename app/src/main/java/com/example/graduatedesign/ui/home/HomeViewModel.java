package com.example.graduatedesign.ui.home;

import android.util.ArrayMap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.data.pagingsource.ActivityPagingSourceParams;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

@HiltViewModel
public class HomeViewModel extends ViewModel {
    private CoroutineScope scope = ViewModelKt.getViewModelScope(this);
    private final MyRepository myRepository;

    /* 要传递给子fragment的搜索数据 */
    private int tabOpt = 0;
    private MutableLiveData<String> searchKey = new MutableLiveData<>();

    @Inject
    public HomeViewModel(MyRepository myRepository) {
        this.myRepository = myRepository;
    }

    public int getTabOpt() {
        return tabOpt;
    }

    public MutableLiveData<String> getSearchKey() {
        return searchKey;
    }

    public void setTabOpt(int tabOpt) {
        this.tabOpt = tabOpt;
    }

    /**
     * 返回一个新创建的分页查询对象
     * @param size 每页大小
     * @param schoolId 学校id
     * @param key 查询关键词
     * @return
     */
    public Flowable<PagingData<MyStudentActivity>> getActivities(Integer size, Integer schoolId, String key) {
        ArrayMap params = ActivityPagingSourceParams.getParams();
        if (schoolId != null)
            params.put("schoolId", schoolId);
        if (key != null)
            params.put("key", key);
        else params.put("key", null);
        return PagingRx.cachedIn(myRepository.getActivities(size), scope);
    }

}
