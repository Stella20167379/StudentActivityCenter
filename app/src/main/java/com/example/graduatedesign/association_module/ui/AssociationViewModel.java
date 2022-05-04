package com.example.graduatedesign.association_module.ui;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.graduatedesign.association_module.data.Association;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.ui.bulletin.data.SimpleBulletin;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AssociationViewModel extends ViewModel {
    private final MyRepository repository;
    private MutableLiveData<Association> associationToShowDetail=new MutableLiveData<>();
    private MutableLiveData<List<MyStudentActivity>> activities=new MutableLiveData<>();
    private MutableLiveData<String> latestBulletin=new MutableLiveData<>();
    private MutableLiveData<String> saveInfoPrompt=new MutableLiveData<>();

    /**
     * 当前用户与当前社团的关系
     * 0-没关系，1-普通会员，2-管理员
     */
    private MutableLiveData<Integer> state=new MutableLiveData<>(0);

    @Inject
    public AssociationViewModel(MyRepository repository) {
        this.repository = repository;
    }

    public MutableLiveData<Association> getAssociationToShowDetail() {
        return associationToShowDetail;
    }

    public MutableLiveData<List<MyStudentActivity>> getActivities() {
        return activities;
    }

    public MutableLiveData<String> getLatestBulletin() {
        return latestBulletin;
    }

    public MutableLiveData<Integer> getState() {
        return state;
    }


    public MutableLiveData<String> getSaveInfoPrompt() {
        return saveInfoPrompt;
    }

    /**
     * 查询 当前要展示的社团详情对象 + 该社团最新一条公告 + 该社团关联的活动 + 当前用户跟当前社团的关系
     * @param associationId 当前要查看详情的社团id
     */
    public void initData(int associationId){

    }

    public void uploadImg(Uri uri){

    }

    public void saveEditInfo(Map<String,Object> info){
        String successMsg="保存成功！";
        if (info==null){
            saveInfoPrompt.setValue(successMsg);
            return;
        }
    }
}




