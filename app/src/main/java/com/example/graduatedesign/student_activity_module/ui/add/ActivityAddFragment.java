package com.example.graduatedesign.student_activity_module.ui.add;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.graduatedesign.R;
import com.example.graduatedesign.custom.MyDateTimePicker;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.databinding.FragmentActivityAddBinding;
import com.example.graduatedesign.utils.FileUtil;
import com.example.graduatedesign.utils.PromptUtil;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ActivityAddFragment extends Fragment{
    private static final String TAG = "ActivityAddFragment";
    private FragmentActivityAddBinding binding;
    private View root;
    private Uri coverUri;

    @Inject
    MyRepository repository;
    private ActivityAddPresenter presenter;

    /* 请求权限的对象 */
    private ActivityResultLauncher<String> requestPermissionLauncher;
    /* 打开文件的对象 */
    private ActivityResultLauncher<String> getContentLauncher;
    /* 用户选中的图片uri */
    private ImageView uploadImgView=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActivityAddBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        binding.setIsPay(false);
        presenter=new ActivityAddPresenter(this);

        uploadImgView = binding.uploadImgView;

        /* 定义打开文件，选择确定之后的回调 */
        getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            Log.d(TAG, "用户选中了图片: " + uri);
            showImg(uri);
        });

        /* 注册请求权限通过或拒绝后的回调 */
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Log.d(TAG, "onCreate: 用户同意了申请的权限");
                getContentLauncher.launch("image/*");
            } else {
                PromptUtil.snackbarShowTxt(root, "没有权限，无法进行操作");
                Log.d(TAG, "onCreate: 用户拒绝了申请的权限");
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);
        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        final TextView titleView= binding.titleInput;
        final TextView signStartView = binding.signStartDatetime;
        final TextView signEndView = binding.signEndDatetime;
        final TextView activityStartView = binding.activityStartDatetime;
        final TextView activityEndView = binding.activityEndDatetime;
        final TextView locationView= binding.location;
        final TextView introductionView= binding.introduction;
        final Switch switchPay= binding.switchPay;
        final Button submitBtn= binding.submitBtn;

        signStartView.setOnClickListener(v -> {
            pickDate(signStartView,"报名开始时间",null);
        });

        signEndView.setOnClickListener(v -> {
            pickDate(signEndView,"报名截止时间",signStartView.getText().toString());
        });

        activityStartView.setOnClickListener(v -> {
            pickDate(activityStartView,"活动开始时间",signStartView.getText().toString());
        });

        activityEndView.setOnClickListener(v -> {
            pickDate(activityEndView,"活动截止时间",activityStartView.getText().toString());
        });

        switchPay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.setIsPay(isChecked);
        });

        uploadImgView.setOnClickListener(v->{
            openAlbum();
        });

        //todo:思考如何解耦，这玩意太多了
        submitBtn.setOnClickListener(v->{
            if (coverUri==null){
                PromptUtil.snackbarShowTxt(view,"活动封面不能为空");
                return;
            }
            MyStudentActivity activity=new MyStudentActivity();

            if (switchPay.isChecked()){

                final TextView payNumView=binding.payNumInput;
                final TextView fundUseView=binding.fundUse;
                final TextView chargeAccountView=binding.chargeAccount;
                activity.setFundUse(fundUseView.getText().toString());
                activity.setAlipayAccount(chargeAccountView.getText().toString());
                int chargeAmount;
                try {
                    chargeAmount=Integer.valueOf(payNumView.getText().toString());
                    activity.setChargeAmount(chargeAmount);
                }catch (NumberFormatException e){
                    e.printStackTrace();
                    payNumView.setError("请输入整数！");
                    return;
                }
            }
            activity.setTitle(titleView.getText().toString());
            activity.setSignStart(signStartView.getText().toString());
            activity.setSignEnd(signEndView.getText().toString());
            activity.setActivityStart(activityStartView.getText().toString());
            activity.setActivityEnd(activityEndView.getText().toString());
            activity.setLocation(locationView.getText().toString());
            activity.setIntroduction(introductionView.getText().toString());
            activity.setPayNeed(switchPay.isChecked());

            Single.create(emitter -> {
                File file= FileUtil.from(getContext(),coverUri);
                Log.d("file", "File...:::: uti - "+file .getPath()+" file -" + file + " : " + file .exists());
                emitter.onSuccess(file);
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .to(RxLifecycleUtils.bindLifecycle(this))
                    .subscribe(file -> {
                        presenter.addActivity(repository,activity, (File) file);
                    });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    /**
     * 弹出日期选择框，用户选择后传递给视图控件参数
     * @param view 要显示选择日期的控件
     * @param title 弹框标题
     * @param minDatetime 当前选择器的最小时间
     */
    private void pickDate(TextView view,String title, String minDatetime){
        MyDateTimePicker picker=new MyDateTimePicker();
        picker.setTitle(title);
        if (minDatetime!=null)
            picker.setMinDatetime(minDatetime);
        MyDateTimePicker.MyDateTimePickerDialogListener listener=new MyDateTimePicker.MyDateTimePickerDialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog, String result) {
                view.setText(result);
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                dialog.getDialog().cancel();
            }
        };
        picker.setListener(listener);
        picker.show(requireActivity().getSupportFragmentManager(),"datetimePickerDialog");
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    /**
     * 请求权限
     */
    private void checkPermission(String permission) {
        //请求读取文件的权限
        String writeExternalStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        //查看是否有权限
        if (ContextCompat.checkSelfPermission(
                getContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermission: 系统本身有这个权限");
            getContentLauncher.launch("image/*");
        }
        //评估是否需要向用户展示请求权限的理由
        else if (shouldShowRequestPermissionRationale(permission)) {
            //此处应该告知用户为何需要权限，及按钮
            Log.d(TAG, "checkPermission: 应该向用户显示权限理由");
        }
        //请求权限
        else {
            //自动调用注册的回调
            Log.d(TAG, "checkPermission: 向用户请求权限中");
            requestPermissionLauncher.launch(permission);
        }
    }

    /**
     * 显示图片
     */
    private void showImg(Uri uri) {
        uploadImgView.setImageURI(uri);
        coverUri=uri;
    }

    /**
     * 操作成功提示，自动返回
     */
    public void onAddSuccess(){
        PromptUtil.snackbarShowTxt(root,"成功添加活动！");
        Navigation.findNavController(root).popBackStack();
    }

    public void onAddResult(String msg){
        PromptUtil.snackbarShowTxt(root,msg);
    }

}