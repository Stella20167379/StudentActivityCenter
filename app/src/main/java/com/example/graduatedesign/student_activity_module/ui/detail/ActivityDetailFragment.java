package com.example.graduatedesign.student_activity_module.ui.detail;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.databinding.FragmentActivityDetailBinding;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.student_activity_module.data.PayResult;
import com.example.graduatedesign.student_activity_module.ui.ActivityViewModel;
import com.example.graduatedesign.student_activity_module.ui.RelativeStates;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GlideUtils;
import com.example.graduatedesign.utils.PromptUtil;
import com.example.graduatedesign.utils.QRCodeUtil;
import com.example.graduatedesign.utils.RxLifecycleUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.time.LocalDateTime;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 详情页面
 */
@AndroidEntryPoint
public class ActivityDetailFragment extends Fragment {

    private static final String TAG = "ActivityDetailFragment";
    @Inject
    MyRepository repository;
    private ActivityViewModel viewModel;
    private ActivityDetailPresenter presenter;

    private FragmentActivityDetailBinding binding;
    private View root;
    private Button bottomActionBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActivityDetailBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        bottomActionBtn = binding.bottomActionBtn;
        /* 传入的活动id,注意：签到二维码页面返回后，并没有传入这个数据 */
        Bundle bundle = getArguments();
        if (bundle != null) {
            presenter = new ActivityDetailPresenter(this);
            /* 别忘了加上观察者，才能起作用啊！ */
            getLifecycle().addObserver(presenter);

            viewModel = new ViewModelProvider(this).get(ActivityViewModel.class);
            viewModel.setActivityId(bundle.getInt("id"));
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewModel == null)
            return;

        final MainActivityViewModel activityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        User user = activityViewModel.getUserInfo().getValue();
        int activityId = viewModel.getActivityId();
        int userId = user.getId();

        bottomActionBtn.setOnClickListener(v -> {
            switch (viewModel.getBtnEventState()) {
                case RelativeStates.BtnSignState:
                    if (viewModel.getActivityToShowDetail().getValue().isPayNeed()) {
                        /* 已安装支付宝沙箱 */
                        if (checkHasAlipay()) {
                            PromptUtil.showAlert(getContext(), "即将调起支付宝支付", dialog -> {
                                dialog.cancel();
                                presenter.getSignedAlipayInfo(userId, repository, activityId);
                            });
                        }
                        /* 未安装支付宝沙箱 */
                        else {
                            /* 获取服务器签发的订单，成功后回调打开支付宝支付操作 */
                            PromptUtil.showAlert(getContext(), "您未安装支付宝，将使用二维码收费", dialog -> {
                                dialog.cancel();
                                openQRCodeFragment(RelativeStates.ActionPay, userId, activityId);
                            });

                        }
                    } else
                        presenter.signToActivity(userId, repository, activityId);
                    break;
                case RelativeStates.BtnAdminState:
                    final NavController navController = Navigation.findNavController(root);
                    Bundle bundle = new Bundle();
                    bundle.putInt("userId", userId);
                    bundle.putInt("activityId", activityId);
                    navController.navigate(R.id.myCaptureFragment, bundle);
                    break;
                case RelativeStates.BtnCheckInState:
                    /* 打开显示二维码的页面 */
                    openQRCodeFragment(RelativeStates.ActionCheckIn, userId, activityId);
                    break;
                case RelativeStates.BtnCheckOutState:
                    openQRCodeFragment(RelativeStates.BtnCheckOutState, userId, activityId);
                    break;
            }
        });

        /*初始化数据*/
        presenter.initDetail(userId, repository, activityId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    /**
     * 数据初始化成功的回调
     *
     * @param data
     */
    public void onInitSuccess(Map<String, Object> data) {
        /* map转换为pojo */
        Gson gson = new Gson();
        Map<String, Object> activityMap = (Map<String, Object>) data.get("activity");
        JsonElement jsonElement = gson.toJsonTree(activityMap);
        MyStudentActivity activity = gson.fromJson(jsonElement, MyStudentActivity.class);

        if (activity == null) {
            PromptUtil.snackbarShowTxt(root, "数据初始化失败");
            Navigation.findNavController(root).popBackStack();
            return;
        } else {
            /* 设置用户与活动的关系 */
            Log.d(TAG, "onInitSuccess: activity.getParticipantState()  " + activity.getParticipantState());
            viewModel.setParticipantState(activity.getParticipantState());

            bindToViews(activity);

            /* 在当前页面生命周期内，缓存信息 */
            viewModel.getActivityToShowDetail().setValue(activity);
            final TextView bulletinView = binding.bulletin;
            String latestBulletin = (String) data.get("latestBulletin");
            bulletinView.setText(latestBulletin);
        }
    }

    public void showMsg(String msg) {
        PromptUtil.snackbarShowTxt(root, msg);
    }

    /**
     * 将对象信息显示到控件上
     *
     * @param activity 除了基本信息外，还包括用户在当前活动的参与状态
     */
    private void bindToViews(MyStudentActivity activity) {
        if (activity != null) {
            final ImageView coverView = binding.cover;
            final TextView titleView = binding.activityTitle;
            final TextView associationNameView = binding.associationName;
            final TextView signTimeView = binding.signTime;
            final TextView activityTimeView = binding.activityTime;
            final TextView locationView = binding.location;
            final TextView introductionView = binding.introduction;

            Glide.with(this)
                    .load(GlideUtils.getImgDownloadUri(activity.getCoverImg()))
                    .apply(GlideUtils.OPTIONS)
                    .into(coverView);
            titleView.setText(activity.getTitle());
            associationNameView.setText(activity.getAssociationName());
            signTimeView.setText(activity.getSignStart() + "-" + activity.getSignEnd());
            activityTimeView.setText(activity.getActivityStart() + "-" + activity.getActivityEnd());
            locationView.setText(activity.getLocation());
            introductionView.setText(activity.getIntroduction());

            if (activity.isPayNeed()) {
                binding.setIsPayNeed(true);
                final TextView chargeAmountView = binding.chargeAmount;
                final TextView fundUseView = binding.fundUse;
                chargeAmountView.setText(String.valueOf(activity.getChargeAmount()));
                fundUseView.setText(activity.getFundUse());
            }

            /* 根据活动各种时间及用户当前与活动的关系来改变底部按钮的文字、状态及点击事件类别 */
            int signState = checkSignState(activity.getSignStart(), activity.getSignEnd());
            int activityState = checkActivityState(activity.getActivityStart(), activity.getActivityEnd());
            checkUserState(signState, activityState);
        }
    }

    /**
     * 查看活动当前的报名状态
     *
     * @param start 活动报名开始时间
     * @param end   活动报名截止时间
     * @return
     */
    private int checkSignState(String start, String end) {
        int state = RelativeStates.SignDisableState;
        String nowStr = DataUtil.dateToString(LocalDateTime.now(), true);
        if (DataUtil.compareDatetimeStr(start, nowStr))
            state = RelativeStates.SignNotStartState;
        else if (DataUtil.compareDatetimeStr(end, nowStr))
            state = RelativeStates.SignAvailableState;
        return state;
    }

    /**
     * 查看活动当前的举行状态
     *
     * @param start 活动开始举行时间
     * @param end   活动结束时间
     * @return
     */
    private int checkActivityState(String start, String end) {
        int state = RelativeStates.ActivityEndState;
        String nowStr = DataUtil.dateToString(LocalDateTime.now(), true);
        if (DataUtil.compareDatetimeStr(start, nowStr))
            state = RelativeStates.ActivityNotStartState;
        else if (DataUtil.compareDatetimeStr(end, nowStr))
            state = RelativeStates.ActivityStartState;
        return state;
    }

    /**
     * 检查用户和活动的对应状态，以为底部按钮设置响相应状态
     *
     * @param signState     活动当前是否开始报名的状态
     * @param activityState 活动当前是否开始举行的状态
     */
    private void checkUserState(int signState, int activityState) {

        if (signState == RelativeStates.SignNotStartState) {
            bottomActionBtn.setEnabled(false);
            bottomActionBtn.setText(R.string.special_activity_state_1);
            return;
        }

        switch (viewModel.getParticipantState()) {
            case RelativeStates.RelAlreadyCheckOutState:
                bottomActionBtn.setEnabled(false);
                bottomActionBtn.setText(R.string.special_activity_state_8);
                break;
            case RelativeStates.RelNoneState:
                if (signState == RelativeStates.SignAvailableState) {
                    bottomActionBtn.setText(R.string.special_activity_state_2);
                    viewModel.setBtnEventState(RelativeStates.BtnSignState);
                } else if (activityState == RelativeStates.ActivityEndState) {
                    bottomActionBtn.setEnabled(false);
                    bottomActionBtn.setText(R.string.special_activity_state_9);
                } else {
                    bottomActionBtn.setEnabled(false);
                    bottomActionBtn.setText(R.string.special_activity_state_3);
                }
                break;
            case RelativeStates.RelAdminState:
                if (activityState == RelativeStates.ActivityEndState) {
                    bottomActionBtn.setEnabled(false);
                    bottomActionBtn.setText(R.string.special_activity_state_8);
                } else if (activityState == RelativeStates.ActivityStartState) {
                    viewModel.setBtnEventState(RelativeStates.BtnAdminState);
                    bottomActionBtn.setText(R.string.special_activity_state_4);
                } else {
                    bottomActionBtn.setEnabled(false);
                    bottomActionBtn.setText(R.string.special_activity_state_1);
                }
                break;
            case RelativeStates.RelNotCheckInState:
                if (activityState != RelativeStates.ActivityEndState) {
                    viewModel.setBtnEventState(RelativeStates.BtnCheckInState);
                    bottomActionBtn.setText(R.string.special_activity_state_5);
                } else {
                    bottomActionBtn.setEnabled(false);
                    bottomActionBtn.setText(R.string.special_activity_state_7);
                }
                break;
            case RelativeStates.RelNotCheckOutState:
                if (activityState == RelativeStates.ActivityEndState) {
                    bottomActionBtn.setEnabled(false);
                    bottomActionBtn.setText(R.string.special_activity_state_7);
                } else {
                    viewModel.setBtnEventState(RelativeStates.BtnCheckOutState);
                    bottomActionBtn.setText(R.string.special_activity_state_6);
                }
                break;
            default:
                PromptUtil.showAlert(getContext(), "没有找到当前用户与该活动的关联关系！");
        }
    }

    /**
     * @return 是否安装了支付宝（沙箱）
     */
    private boolean checkHasAlipay() {
        final String ali_package_name = "com.eg.android.AlipayGphoneRC";
//        Uri uri = Uri.parse("alipays://platformapi/startApp");
        try {
            requireActivity().getPackageManager().getPackageInfo(ali_package_name, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 打开显示二维码的页面，传入关键信息使其生成二维码显示
     *
     * @param actionType 付费、签到、签退
     * @param userId     用户id
     * @param activityId 社团id
     */
    private void openQRCodeFragment(int actionType, int userId, int activityId) {
        Bundle bundle = new Bundle();
        String separator = QRCodeUtil.strSeparator;
        String content = "" + userId + separator + activityId;
        bundle.putString("content", content);
        final NavController navController = Navigation.findNavController(root);

        switch (actionType) {
            case RelativeStates.ActionPay:
                bundle.putInt("type", RelativeStates.ActionPay);
                bundle.putString("title", "支付");
                bundle.putString("prompt", "请保存二维码后，使用支付宝扫描支付");
                break;
            case RelativeStates.ActionCheckIn:
                bundle.putInt("type", RelativeStates.ActionCheckIn);
                bundle.putString("title", "签到");
                bundle.putString("prompt", "活动管理员扫描签到");
                break;
            case RelativeStates.ActionCheckOut:
                bundle.putInt("type", RelativeStates.ActionCheckOut);
                bundle.putString("title", "签退");
                bundle.putString("prompt", "活动管理员扫描签退");
                break;
        }
        navController.navigate(R.id.QRCodeShowFragment, bundle);
    }

    /**
     * 报名活动成功的回调操作
     */
    public void onSignSuccess() {
        PromptUtil.snackbarShowTxt(root, "报名成功！");

        MyStudentActivity activity = viewModel.getActivityToShowDetail().getValue();
        /* 根据活动时间更新报名成功后，底部按钮的文字、事件类型 */
        int activityState = checkActivityState(activity.getActivityStart(), activity.getActivityEnd());
        switch (activityState) {
            case RelativeStates.ActivityNotStartState:
                bottomActionBtn.setEnabled(false);
                bottomActionBtn.setText(R.string.special_activity_state_1);
                break;
            case RelativeStates.ActivityStartState:
                bottomActionBtn.setEnabled(true);
                viewModel.setBtnEventState(RelativeStates.BtnCheckInState);
                bottomActionBtn.setText(R.string.special_activity_state_5);
                break;
            case RelativeStates.ActivityEndState:
                bottomActionBtn.setEnabled(false);
                bottomActionBtn.setText(R.string.special_activity_state_7);
                break;
        }
    }

    /**
     * 成功收到服务器签发的订单后，回调操作
     *
     * @param orderInfo 服务器签名后的订单信息
     */
    public void onSignOrderInfoSuccess(final String orderInfo) {
        Single.create(emitter -> {
            /* 设置为沙箱支付环境 */
            EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
            PayTask alipay = new PayTask(requireActivity());
            Map<String, String> result = alipay.payV2(orderInfo, true);
            Log.i("msp", result.toString());
            emitter.onSuccess(result);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(this))
                .subscribe(result -> {
                            PayResult payResult = new PayResult((Map<String, String>) result);
                            String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                            String resultStatus = payResult.getResultStatus();
                            // 判断resultStatus 为9000则代表支付成功
                            if (TextUtils.equals(resultStatus, "9000")) {
                                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                                PromptUtil.showAlert(getContext(), getString(R.string.txt_pay_success) + payResult);
                            } else {
                                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                                PromptUtil.showAlert(getContext(), getString(R.string.txt_pay_failed) + resultInfo);
                            }
                        }, throwable -> {
                            PromptUtil.showAlert(getContext(), "发生错误，支付失败!");
                        }
                );
    }

    /**
     * 活动签到成功的回调操作
     */
    @Deprecated
    public void onCheckInSuccess() {
        PromptUtil.snackbarShowTxt(root, "签到成功！");

        MyStudentActivity activity = viewModel.getActivityToShowDetail().getValue();
        /* 根据活动时间更新签到成功后，底部按钮的文字、事件类型 */
        int activityState = checkActivityState(activity.getActivityStart(), activity.getActivityEnd());
        if (activityState != RelativeStates.ActivityEndState) {
            bottomActionBtn.setEnabled(true);
            viewModel.setBtnEventState(RelativeStates.BtnCheckOutState);
            bottomActionBtn.setText(R.string.special_activity_state_5);
        } else {
            bottomActionBtn.setEnabled(false);
            bottomActionBtn.setText(R.string.special_activity_state_7);
        }
    }

    /**
     * 活动签退成功的回调操作
     */
    @Deprecated
    public void onCheckOutSuccess() {
        PromptUtil.snackbarShowTxt(root, "签退成功！");
        /* 签退后，无论活动是否已经结束，底部按钮都无效且显示已签退 */
        bottomActionBtn.setEnabled(false);
        bottomActionBtn.setText(R.string.special_activity_state_8);
    }

}
