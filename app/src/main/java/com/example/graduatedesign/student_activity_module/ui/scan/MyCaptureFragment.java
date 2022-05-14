package com.example.graduatedesign.student_activity_module.ui.scan;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.student_activity_module.ui.RelativeStates;
import com.example.graduatedesign.utils.PromptUtil;
import com.example.graduatedesign.utils.QRCodeUtil;
import com.google.zxing.Result;
import com.king.zxing.CameraScan;
import com.king.zxing.CaptureFragment;
import com.king.zxing.DecodeConfig;
import com.king.zxing.DecodeFormatManager;
import com.king.zxing.analyze.MultiFormatAnalyzer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyCaptureFragment extends CaptureFragment {
    private static final String TAG = "MyCaptureFragment";
    @Inject
    MyRepository repository;
    private MyCapturePresenter presenter;
    private Bundle args;

    public static MyCaptureFragment newInstance(Bundle args) {
        MyCaptureFragment fragment = new MyCaptureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initCameraScan() {
        /* 此函数将在onCreateView()被调用 */
        super.initCameraScan();

        //初始化解码配置
        DecodeConfig decodeConfig = new DecodeConfig();
        decodeConfig.setHints(DecodeFormatManager.QR_CODE_HINTS)//如果只有识别二维码的需求，这样设置效率会更高，不设置默认为DecodeFormatManager.DEFAULT_HINTS
                .setFullAreaScan(false)//设置是否全区域识别，默认false
                .setAreaRectRatio(0.8f)//设置识别区域比例，默认0.8，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别
                .setAreaRectVerticalOffset(0)//设置识别区域垂直方向偏移量，默认为0，为0表示居中，可以为负数
                .setAreaRectHorizontalOffset(0);//设置识别区域水平方向偏移量，默认为0，为0表示居中，可以为负数

        //在启动预览之前，设置分析器，只识别二维码
        getCameraScan()
                .setVibrate(true)//设置是否震动，默认为false
                .setNeedAutoZoom(true)//二维码太小时可自动缩放，默认为false
                .setAnalyzer(new MultiFormatAnalyzer(decodeConfig));//设置分析器,如果内置实现的一些分析器不满足您的需求，你也可以自定义去实现

        presenter = new MyCapturePresenter(this);
        /* 别忘了加上观察者，才能起作用啊！ */
        getLifecycle().addObserver(presenter);

        args = getArguments();
    }

    /**
     * 扫码结果回调
     *
     * @param result
     * @return 返回false表示不拦截，将关闭扫码界面并将结果返回给调用界面；
     * 返回true表示拦截，需自己处理逻辑。当isAnalyze为true时，默认会继续分析图像（也就是连扫）。
     * 如果只是想拦截扫码结果回调，并不想继续分析图像（不想连扫），请在拦截扫码逻辑处通过调
     * 用{@link CameraScan#setAnalyzeImage(boolean)}，
     * 因为{@link CameraScan#setAnalyzeImage(boolean)}方法能动态控制是否继续分析图像。
     */
    @Override
    public boolean onScanResultCallback(Result result) {
        /*
         * 因为setAnalyzeImage方法能动态控制是否继续分析图像。
         *
         * 1. 因为分析图像默认为true，如果想支持连扫，返回true即可。
         * 当连扫的处理逻辑比较复杂时，请在处理逻辑前调用getCameraScan().setAnalyzeImage(false)，
         * 来停止分析图像，等逻辑处理完后再调用getCameraScan().setAnalyzeImage(true)来继续分析图像。
         *
         * 2. 如果只是想拦截扫码结果回调自己处理逻辑，但并不想继续分析图像（即不想连扫），可通过
         * 调用getCameraScan().setAnalyzeImage(false)来停止分析图像。
         */
        //暂停分析图象
        getCameraScan().setAnalyzeImage(false);
        Integer userId = args.getInt("userId");
        Integer activityId = args.getInt("activityId");

        /* 从扫描出来的结果中获取id */
        String content = result.getText();
        Toast.makeText(getContext(), content, Toast.LENGTH_LONG).show();
        Log.d(TAG, "扫描得到内容: " + content);
        String separator = QRCodeUtil.strSeparator;
        String[] contentList = content.split(separator);
        try {
            if (Integer.parseInt(contentList[1]) != activityId) {
                onCheckFail("不是当前活动的二维码！");
                getCameraScan().setAnalyzeImage(true);
                return true;
            }
        } catch (NumberFormatException e) {
            onCheckFail("无法识别该二维码");
            getCameraScan().setAnalyzeImage(true);
            return true;

        }
        presenter.checkInOrOut(Integer.parseInt(contentList[0]), repository, activityId);
        //不暂停了
        return false;
    }

    public void onCheckSuccess(int state) {
        //提示签到/签退成功，继续扫描
        if (state == RelativeStates.RelNotCheckOutState)
            Toast.makeText(getContext(), "签到成功！", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getContext(), "签退成功！", Toast.LENGTH_LONG).show();
        getCameraScan().setAnalyzeImage(true);
    }

    public void onCheckFail(String msg) {
        PromptUtil.showAlert(getContext(), msg, dialog -> {
            dialog.cancel();
            getCameraScan().setAnalyzeImage(true);
        });
    }
}
