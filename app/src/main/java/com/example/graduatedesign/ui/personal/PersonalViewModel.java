package com.example.graduatedesign.ui.personal;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class PersonalViewModel extends ViewModel {
    private final MyRepository myRepository;
    private MutableLiveData<List<MyStudentActivity>> myActivities=new MutableLiveData<>();

    @Inject
    public PersonalViewModel(MyRepository myRepository){
        this.myRepository = myRepository;
    }

    public MutableLiveData<List<MyStudentActivity>> getMyActivities() {
        return myActivities;
    }

    /**
     * 查询当前用户参与的活动，显示总览
     *
     * @param lifecycleOwner 拥有生命周期的视图，用于绑定rxjava流的有效作用域，在onStop()情况下自动结束流
     * @param userId         当前用户id
     */
    public void initMyActivities(LifecycleOwner lifecycleOwner, int userId) {
        myRepository.getActivitiesForUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(lifecycleOwner))
                .subscribe(activities -> {
                            myActivities.setValue(activities);
                        }, throwable -> {
                            throwable.printStackTrace();
                        }
                );
    }


}