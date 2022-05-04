package com.example.graduatedesign.personal_module.ui.detail;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.utils.FileUtil;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import java.io.File;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
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
        view=null;
    }

    public void initData(MyRepository repository, int userId){

    }

    /**
     * 上传修改后的头像图片
     */
    public void uploadImg(MyRepository repository,Uri uri) {

    }

    /**
     * 向服务器请求更新用户资料
     * @param info 修改后的数据
     */
    public void saveEditInfo(MyRepository repository, Map<String,Object> info) {
        if (info==null)
            return;

    }
}
