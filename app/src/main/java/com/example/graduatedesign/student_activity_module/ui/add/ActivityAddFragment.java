package com.example.graduatedesign.student_activity_module.ui.add;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.adapter.AssociationSpinnerAdapter;
import com.example.graduatedesign.association_module.data.Association;
import com.example.graduatedesign.custom.MyDateTimePicker;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.data.model.Result;
import com.example.graduatedesign.databinding.FragmentActivityAddBinding;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.student_activity_module.ui.ActivityViewModel;
import com.example.graduatedesign.student_activity_module.ui.RelativeStates;
import com.example.graduatedesign.utils.FileUtil;
import com.example.graduatedesign.utils.PromptUtil;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class ActivityAddFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "ActivityAddFragment";
    private FragmentActivityAddBinding binding;
    private View root;
    private Uri coverUri;

    private ActivityViewModel viewModel;
    /**
     * 对viewModel中数据的引用，保存用户当前编辑的信息
     */
    private MyStudentActivity activityEditing;

    /* 请求权限的对象 */
    private ActivityResultLauncher<String> requestPermissionLauncher;
    /* 打开文件的对象 */
    private ActivityResultLauncher<String> getContentLauncher;
    /* 用户选中的图片uri */
    private ImageView uploadImgView = null;

    /**
     * 本数据不能为空
     * 1-由社团详情界面进入本页面，则传入associationId+name
     * 2-由首页进打开页面，则传入userId
     */
    private Bundle args;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActivityAddBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        viewModel = new ViewModelProvider(requireActivity()).get(ActivityViewModel.class);

        uploadImgView = binding.uploadImgView;

        args = getArguments();
        assert args != null;

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);
        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
                    //主动退出页面，清除缓存的信息
                    viewModel.getActivityEditing().setValue(null);
                    navController.popBackStack();
                }
        );

        activityEditing = viewModel.getActivityEditing().getValue();
        //刚进入页面时没数据需要初始化
        if (activityEditing == null) {
            activityEditing = new MyStudentActivity();
            viewModel.getActivityEditing().setValue(activityEditing);
        }
        //
        final User user = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class).getUserInfo().getValue();
        activityEditing.setCollegeId(user.getSchoolId());

        final TextView titleView = binding.titleInput;
        final TextView signStartView = binding.signStartDatetime;
        final TextView signEndView = binding.signEndDatetime;
        final TextView activityStartView = binding.activityStartDatetime;
        final TextView activityEndView = binding.activityEndDatetime;
        final TextView locationView = binding.location;
        final TextView introductionView = binding.introduction;
        final TextView payNumView = binding.payNumInput;
        final TextView chargeAccountView = binding.chargeAccount;
        final TextView fundUseView = binding.fundUse;
        final Switch switchPay = binding.switchPay;
        final Button submitBtn = binding.submitBtn;
        final Spinner associationSpinner = binding.associationSpinner;

        /* 下拉框添加适配器 */
        associationSpinner.setOnItemSelectedListener(this);

        /*---------------------------------------- 文字变化事件 ----------------------------------------------*/
        titleView.addTextChangedListener(createWatcher(() -> {
            String value = titleView.getText().toString();
            activityEditing.setTitle(value);
        }));

        signStartView.addTextChangedListener(createWatcher(() -> activityEditing.setSignStart(signStartView.getText().toString())));

        signEndView.addTextChangedListener(createWatcher(() -> activityEditing.setSignEnd(signEndView.getText().toString())));

        activityStartView.addTextChangedListener(createWatcher(() -> activityEditing.setActivityStart(activityStartView.getText().toString())));

        activityEndView.addTextChangedListener(createWatcher(() -> activityEditing.setActivityEnd(activityEndView.getText().toString())));

        locationView.addTextChangedListener(createWatcher(() -> activityEditing.setLocation(locationView.getText().toString())));

        introductionView.addTextChangedListener(createWatcher(() -> activityEditing.setIntroduction(introductionView.getText().toString())));

        payNumView.addTextChangedListener(createWatcher(() -> {
            if (activityEditing.isPayNeed()) {
                String value = payNumView.getText().toString();
                activityEditing.setChargeAmount(Integer.parseInt(value));
            }
        }));
        chargeAccountView.addTextChangedListener(createWatcher(() -> {
            if (activityEditing.isPayNeed())
                activityEditing.setAlipayAccount(chargeAccountView.getText().toString());
        }));
        fundUseView.addTextChangedListener(createWatcher(() -> {
            if (activityEditing.isPayNeed())
                activityEditing.setFundUse(fundUseView.getText().toString());
        }));

        /*-------------------------------------- 点击监听事件 ------------------------------------------*/
        signStartView.setOnClickListener(v -> pickDate(signStartView, "报名开始时间", null));

        /* 后端返回来的时间带秒，此处选择的时间不带秒，为了方便，干脆不设限了 */
        signEndView.setOnClickListener(v -> pickDate(signEndView, "报名截止时间", null));

        activityStartView.setOnClickListener(v -> pickDate(activityStartView, "活动开始时间", null));

        activityEndView.setOnClickListener(v -> pickDate(activityEndView, "活动截止时间", null));

        switchPay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            activityEditing.setPayNeed(isChecked);
            binding.setIsPay(isChecked);
        });

        uploadImgView.setOnClickListener(v -> {
            openAlbum();
        });

        submitBtn.setOnClickListener(v -> {
            if (coverUri == null) {
                PromptUtil.snackbarShowTxt(view, "活动封面不能为空");
                return;
            }
            //防止重复点击
            uploadImgView.setEnabled(false);
            submitBtn.setEnabled(false);

            /* 收集图片数据 */
            Single.create(emitter -> {
                File file = FileUtil.from(getContext(), this.coverUri);
                emitter.onSuccess(file);
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .to(RxLifecycleUtils.bindLifecycle(this))
                    .subscribe(file -> viewModel.addActivity((File) file, this)
                            , throwable -> PromptUtil.snackbarShowTxt(root, "图片文件创建失败！"));
        });

        /*-------------------------------------- viewModel数据变化监听事件 -------------------------------------*/
        viewModel.getAdminAssociations().observe(getViewLifecycleOwner(), associations -> {
            AssociationSpinnerAdapter adapter = new AssociationSpinnerAdapter(associations);
            associationSpinner.setAdapter(adapter);
        });

        /* 用户点击确定按钮提交后的结果回调 */
        viewModel.getAddActivityResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            if (result instanceof Result.Success) {
                PromptUtil.snackbarShowTxt(root, "活动添加成功！");
                //清空缓存的编辑信息
                viewModel.getActivityEditing().setValue(null);
                navController.popBackStack();
            } else {
                submitBtn.setEnabled(true);
                uploadImgView.setEnabled(true);
                PromptUtil.snackbarShowTxt(root, "失败：" + result.toString());
            }
            //要清空，否则进入添加页面时会弹出提醒
            viewModel.getAddActivityResult().setValue(null);
        });

        /* 选择图片后返回应用，系统将重建本页面，并触发viewModel数据更新事件，所以需要手动将数据显示到页面上 */
        viewModel.getActivityEditing().observe(getViewLifecycleOwner(), activity -> {
            if (activity != null) {
                titleView.setText(activity.getTitle());
                signStartView.setText(activity.getSignStart());
                signEndView.setText(activity.getSignEnd());
                activityStartView.setText(activity.getActivityStart());
                activityEndView.setText(activity.getActivityEnd());
                locationView.setText(activity.getLocation());
                introductionView.setText(activity.getIntroduction());
                boolean isPay = activity.isPayNeed();
                binding.setIsPay(isPay);
                if (isPay) {
                    /* 特别注意，对int类型，系统会误认为资源id */
                    payNumView.setText(String.valueOf(activity.getChargeAmount()));
                    chargeAccountView.setText(activity.getAlipayAccount());
                    fundUseView.setText(activity.getFundUse());
                }
            }
        });

        /*-------------------------------------- 初始化数据 -------------------------------------*/
        /* 由社团详情页面进入本页面时，注意：getInt不传默认值时系统自动设 0 */
        int type = args.getInt("type");
        switch (type) {
            case RelativeStates.FromNone:
                PromptUtil.showAlert(getContext(), "缺少必要参数，操作失败请返回");
                break;
            case RelativeStates.FromHomeFragment:
                Log.d(TAG, "主页进入");
                Integer userId = args.getInt("userId");
                viewModel.selectAdminAssociations(userId, this);
                break;
            case RelativeStates.FromAssociationFragment:
                Log.d(TAG, "o社团进入");
                int associationId = args.getInt("associationId");
                String name = args.getString("name");
                Association association = new Association();
                association.setId(associationId);
                association.setAssociationName(name);
                List<Association> associationList = new ArrayList<>();
                associationList.add(association);
                AssociationSpinnerAdapter adapter = new AssociationSpinnerAdapter(associationList);
                associationSpinner.setAdapter(adapter);
                break;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    /**
     * 弹出日期选择框，用户选择后传递给视图控件参数
     *
     * @param view        要显示选择日期的控件
     * @param title       弹框标题
     * @param minDatetime 当前选择器的最小时间
     */
    private void pickDate(TextView view, String title, String minDatetime) {
        MyDateTimePicker picker = new MyDateTimePicker();
        picker.setTitle(title);
        if (minDatetime != null)
            picker.setMinDatetime(minDatetime);
        MyDateTimePicker.MyDateTimePickerDialogListener listener = new MyDateTimePicker.MyDateTimePickerDialogListener() {
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
        picker.show(requireActivity().getSupportFragmentManager(), "datetimePickerDialog");
    }

    /**
     * @param callBack 监听到文字变化后的回调操作
     */
    private TextWatcher createWatcher(TextViewCallBack callBack) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                callBack.onChanged();
            }
        };
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
        coverUri = uri;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Association association = (Association) parent.getSelectedItem();
        if (association != null) {
            MyStudentActivity activity = viewModel.getActivityEditing().getValue();
            activity.setAssociationName(association.getAssociationName());
            activity.setAssociationId(association.getId());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * 回调用
     */
    private interface TextViewCallBack {
        void onChanged();
    }
}