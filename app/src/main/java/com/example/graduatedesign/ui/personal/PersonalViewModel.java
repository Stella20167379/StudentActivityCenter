package com.example.graduatedesign.ui.personal;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.personal_module.data.User;

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
     * @param userId 当前用户id
     */
    public void initMyActivities(int userId){

    }
}