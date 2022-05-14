package com.example.graduatedesign.personal_module.ui.detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import java.io.File;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PersonalDetailPresenter implements DefaultLifecycleObserver {
    private PersonalDetailFragment view;

    public PersonalDetailPresenter(PersonalDetailFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        //删除对框架类fragment的引用,系统才能成功回收资源
        view = null;
    }

    /**
     * 初始查询展示数据
     *
     * @param userId 用户id
     */
    public void initData(MyRepository repository, int userId) {
        repository.getPersonalDetailForUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(user -> view.onInitSuccess(user),
                        throwable -> {
                            throwable.printStackTrace();
                            view.onInitFail();
                        });

    }

    /**
     * 向服务器请求更新用户资料
     *
     * @param info 修改后的文字数据
     * @param file 用户修改后图片后系统生成的图片文件备份
     */
    public void saveEditInfo(MyRepository repository, Map<String, String> info, File file) {
        if (info == null)
            view.onSavePersonalEditFail("保存失败:缺少用户id！");

        RequestBody fileRQ = null;
        if (file != null)
            fileRQ = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        RequestBody body = DataUtil.buildRequestBody(fileRQ, info);
        repository.savePersonalEditWithFile(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(
                        data -> {
                            view.onSavePersonalEditSuccess(data);
                        }
                        , throwable -> {
                            throwable.printStackTrace();
                            view.onSavePersonalEditFail("信息保存失败:" + throwable.getMessage());
                        }
                );
    }
}
