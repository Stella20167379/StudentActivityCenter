package com.example.graduatedesign.student_activity_module.ui.scan;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.net.RetrofitExceptionResolver;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyCapturePresenter implements DefaultLifecycleObserver {
    private MyCaptureFragment view;

    public MyCapturePresenter(MyCaptureFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        view = null;
    }

    /**
     * 请求服务器，实现签到/签退操作
     *
     * @param userId     用户id
     * @param activityId 活动id
     */
    public void checkInOrOut(int userId, MyRepository repository, int activityId) {
        repository.checkInOrOut(userId, activityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(
                        state -> {
                            view.onCheckSuccess(state);
                        }, throwable -> {
                            throwable.printStackTrace();
                            view.onCheckFail("操作失败，" + RetrofitExceptionResolver.resolve(throwable));
                        }
                );
    }
}
