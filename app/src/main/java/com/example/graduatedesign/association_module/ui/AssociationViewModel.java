package com.example.graduatedesign.association_module.ui;

import android.view.View;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.graduatedesign.association_module.data.Association;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GsonConvertTypes;
import com.example.graduatedesign.utils.RxLifecycleUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

@HiltViewModel
public class AssociationViewModel extends ViewModel {
    private static final String TAG = "AssociationViewModel";
    private final MyRepository repository;
    private final Gson gson;
    private MutableLiveData<Association> associationToShowDetail = new MutableLiveData<>();
    private MutableLiveData<List<MyStudentActivity>> activities = new MutableLiveData<>();
    private MutableLiveData<String> latestBulletin = new MutableLiveData<>();
    private MutableLiveData<String> saveInfoPrompt = new MutableLiveData<>();
    private MutableLiveData<String> newImgPath = new MutableLiveData<>();

    /**
     * 当前用户与当前社团的关系
     * 0-没关系，1-普通会员，2-管理员
     */
    private MutableLiveData<Integer> state = new MutableLiveData<>();

    @Inject
    public AssociationViewModel(MyRepository repository, Gson gson) {
        this.repository = repository;
        this.gson = gson;
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

    public MutableLiveData<String> getNewImgPath() {
        return newImgPath;
    }

    /**
     * 查询 当前要展示的社团详情对象 + 该社团最新一条公告 + 该社团关联的活动 + 当前用户跟当前社团的关系
     *
     * @param userId
     * @param associationId 当前要查看详情的社团id
     */
    public void initData(int userId, View view, int associationId) {
        repository.getAssociationDetailShowData(userId, associationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(map -> {
                            /* list<map>转换为pojo */
                            List activityList = (List) map.get("activityList");
                            JsonElement listElement = gson.toJsonTree(activityList);
                            List<MyStudentActivity> resultActivities = gson.fromJson(listElement, GsonConvertTypes.gsonTypeOfActivityList);
                            this.activities.setValue(resultActivities);
                            /* map转换为pojo */
                            Map<String, Object> association = (Map<String, Object>) map.get("association");
                            JsonElement jsonElement = gson.toJsonTree(association);
                            Association resultAssociation = gson.fromJson(jsonElement, Association.class);
                            this.associationToShowDetail.setValue(resultAssociation);

                            /* 由于状态变化会引起界面菜单渲染，菜单渲染又需要社团信息，故置于此处 */
                            Integer state = DataUtil.getIntFromGsonMap(map, "state");
                            String latestBulletin = (String) map.get("latestBulletin");
                            this.state.setValue(state);
                            this.latestBulletin.setValue(latestBulletin);

                        }, throwable -> {
                            throwable.printStackTrace();
                            associationToShowDetail.setValue(null);
                        }
                );
    }

    /**
     * 保存管理员对社团资料的修改
     *
     * @param info 修改后的信息,必须包含社团id
     * @param file 修改后的头像文件
     */
    public void saveEditInfo(LifecycleOwner lifecycleOwner, Map<String, String> info, File file) {
        String successMsg = "保存成功！";
        if (info == null)
            saveInfoPrompt.setValue("保存失败:缺少社团id！ ");

        RequestBody fileRQ = null;
        if (file != null)
            fileRQ = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        RequestBody body = DataUtil.buildRequestBody(fileRQ, info);
        repository.saveAssociationEditWithFile(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(lifecycleOwner))
                .subscribe(
                        str -> {
                            saveInfoPrompt.setValue(successMsg);
                            /* 更新缓存中的社团资料 */
                            Association association = this.associationToShowDetail.getValue();
                            if (str != null) {
                                //提示详情显示界面刷新头像
                                this.newImgPath.setValue(str);
                                association.setCoverImg(str);
                            }
                            String var1 = info.get("associationName");
                            String var2 = info.get("introduction");
                            if (var1 != null)
                                association.setAssociationName(var1);
                            if (var2 != null)
                                association.setIntroduction(var2);
                        }
                        , throwable -> {
                            throwable.printStackTrace();
                            saveInfoPrompt.setValue("保存失败:" + throwable.getMessage());
                        }
                );
    }

}




