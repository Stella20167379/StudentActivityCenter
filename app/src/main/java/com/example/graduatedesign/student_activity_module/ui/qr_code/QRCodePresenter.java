package com.example.graduatedesign.student_activity_module.ui.qr_code;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.net.RetrofitExceptionResolver;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QRCodePresenter implements DefaultLifecycleObserver {
    private QRCodeShowFragment view;

    public QRCodePresenter(QRCodeShowFragment view) {
        this.view = view;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        view = null;
    }

    /**
     * 请求服务器生成收款二维码图片，显示
     *
     * @param userId     用户id
     * @param activityId 活动id
     */
    public void getAlipayQrCodeBitMap(int userId, MyRepository repository, int activityId) {
        repository.getAlipayQrCodePath(userId, activityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(view))
                .subscribe(
                        path -> {
                            view.onSignInfoSuccess(path);
                        }, throwable -> {
                            throwable.printStackTrace();
                            view.onSignInfoFail(RetrofitExceptionResolver.resolve(throwable));
                        }
                );
    }

}
