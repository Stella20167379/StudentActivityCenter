package com.example.graduatedesign.student_activity_module.ui.detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ActivityDetailPresenter implements DefaultLifecycleObserver {
    private ActivityDetailFragment view;

    public ActivityDetailPresenter(ActivityDetailFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        view = null;
    }

    /**
     * 查询活动详情信息，其包含当前用户与该活动的参与关系
     *
     * @param userId     用户id
     * @param activityId 活动id
     */
    public void initDetail(int userId, MyRepository repository, int activityId) {
        repository.getActivityDetailShowData(userId, activityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(map -> {
                            view.onInitSuccess(map);
                        }, throwable -> {
                            throwable.printStackTrace();
                            view.showMsg("错误：" + throwable.getMessage());
                        }
                );
    }

    /**
     * 用户报名活动
     *
     * @param userId     用户id
     * @param activityId 活动id
     */
    public void signToActivity(int userId, MyRepository repository, int activityId) {
        repository.signToActivity(userId, activityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(isSuccess -> {
                    if (isSuccess)
                        view.onSignSuccess();
                    else
                        view.showMsg("报名失败！");
                }, throwable -> {
                    throwable.printStackTrace();
                    view.showMsg("错误：" + throwable.getMessage());
                });
    }

    /**
     * 用户报名付费活动，且已安装支付宝沙箱
     *
     * @param userId     用户id
     * @param activityId 活动id
     * @return 签名后的订单信息，用以拉起支付宝支付
     */
    public void getSignedAlipayInfo(int userId, MyRepository repository, int activityId) {
        repository.getSignedAlipayInfo(userId, activityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(info -> {
                    assert info != null && info.trim().length() > 0;
                    view.onSignOrderInfoSuccess(info);
                }, throwable -> {
                    throwable.printStackTrace();
                    view.showMsg("支付失败：" + throwable.getMessage());
                });
    }

}