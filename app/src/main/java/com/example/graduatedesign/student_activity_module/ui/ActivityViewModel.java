package com.example.graduatedesign.student_activity_module.ui;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.graduatedesign.association_module.data.Association;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.data.model.Result;
import com.example.graduatedesign.utils.RxLifecycleUtils;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class ActivityViewModel extends ViewModel {
    private static final String TAG = "ActivityViewModel";
    private final MyRepository repository;
    /**
     * 打开页面时传入的，要显示的活动id
     */
    private Integer activityId;
    /**
     * 为区分底部按钮的点击事件而设
     */
    private Integer btnEventState = RelativeStates.BtnSignState;
    /**
     * 用户与当前活动的关系状态，默认为无关联
     */
    private Integer participantState;
    /**
     * 当前展示的活动详情
     */
    private MutableLiveData<MyStudentActivity> activityToShowDetail = new MutableLiveData<>();
    /**
     * 用户添加社团时，保存的编辑信息
     */
    private MutableLiveData<MyStudentActivity> activityEditing = new MutableLiveData<>();
    /**
     * 当前用户管理的社团
     */
    private MutableLiveData<List<Association>> adminAssociations = new MutableLiveData<>();
    /**
     * 添加活动操作的结果
     */
    private MutableLiveData<Result> addActivityResult = new MutableLiveData<>();

    @Inject
    public ActivityViewModel(MyRepository repository) {
        this.repository = repository;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getBtnEventState() {
        return btnEventState;
    }

    public void setBtnEventState(Integer btnEventState) {
        this.btnEventState = btnEventState;
    }

    public MutableLiveData<MyStudentActivity> getActivityToShowDetail() {
        return activityToShowDetail;
    }

    public MutableLiveData<MyStudentActivity> getActivityEditing() {
        return activityEditing;
    }

    public Integer getParticipantState() {
        return participantState;
    }

    public void setParticipantState(Integer participantState) {
        this.participantState = participantState;
    }

    public MutableLiveData<Result> getAddActivityResult() {
        return addActivityResult;
    }

    public MutableLiveData<List<Association>> getAdminAssociations() {
        return adminAssociations;
    }


    /**
     * 查找管理员管理的社团：id+名字，用来初始化社团选择下拉框
     *
     * @param userId 当前拥有社团管理员角色的用户id
     */
    public void selectAdminAssociations(int userId, LifecycleOwner lifecycleOwner) {
        repository.selectAdminAssociations(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(lifecycleOwner))
                .subscribe(
                        associationList -> {
                            Log.d(TAG, "selectAdminAssociations，查询结果: " + (associationList == null));
                            adminAssociations.setValue(associationList);
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            adminAssociations.postValue(null);
                        }
                );
    }

    /**
     * 添加活动
     *
     * @param file           文件
     * @param lifecycleOwner
     */
    public void addActivity(File file, LifecycleOwner lifecycleOwner) {
        MyStudentActivity activity = activityEditing.getValue();
        if (file == null || activity == null) {
            addActivityResult.setValue(new Result.Fail("缺少必要信息！"));
            return;
        }
        if (activity.getAssociationName() == null) {
            addActivityResult.setValue(new Result.Fail("未选择社团！"));
            return;
        }
        RequestBody fileRQ = null;
        if (file != null)
            fileRQ = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        RequestBody body = buildActivityAddBody(fileRQ, activity);
        repository.addActivity(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(lifecycleOwner))
                .subscribe(
                        result -> {
//                            if (result)
                            addActivityResult.setValue(new Result.Success<>(null));
//                            else
//                                addActivityResult.setValue(new Result.Fail("添加失败"));
                        }
                        , throwable -> {
                            throwable.printStackTrace();
                            addActivityResult.setValue(new Result.Error((Exception) throwable));
                        }
                );
    }

    /**
     * 建造请求体
     *
     * @param file     活动封面
     * @param activity 活动信息
     */
    private RequestBody buildActivityAddBody(RequestBody file, MyStudentActivity activity) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        Gson gson = new Gson();
        String activityStr = gson.toJson(activity);
        return builder.addFormDataPart("file", "img", file)
                .addFormDataPart("activity", activityStr)
                .build();
    }

}
