package com.example.graduatedesign.personal_module.ui.detail;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.custom.OneInputDialog;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.databinding.FragmentPersonalDetailBinding;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.utils.FileUtil;
import com.example.graduatedesign.utils.GlideUtils;
import com.example.graduatedesign.utils.PromptUtil;
import com.example.graduatedesign.utils.RxLifecycleUtils;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class PersonalDetailFragment extends Fragment {
    @Inject
    MyRepository repository;
    private PersonalDetailPresenter presenter;

    private static final String TAG = "PersonalDetailFragment";
    private FragmentPersonalDetailBinding binding;
    private View root;

    private ImageView portraitView;
    private TextView nicknameView;
    private TextView emailView;
    private TextView majorClassView;
    //学号
    private TextView principalView;
    private TextView roleView;

    private Button submitBtn;
    private ImageView nicknameEdit;
    private SwitchMaterial sexSwitch;
    private ImageView portraitEdit;

    /* 此为进入页面时MVP查询到的数据 */
    private User previousUser;
    /* 此为修改昵称返回的数据监听key */
    private String requestKey = "PersonalDetailFragment";
    /* 传回数据中所需值的key */
    private String dataKey = "nickname";

    private NavController navController;

    /* 打开文件的对象 */
    private ActivityResultLauncher<String> getContentLauncher;

    /* 请求权限的对象 */
    private ActivityResultLauncher<String> requestPermissionLauncher;

    /* 用户选中的图片uri */
    private Uri portraitUri = null;
    /* 提交修改时，保存修改后信息的对象 */
    private Map<String, String> editInfo = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: 生命周期");

        /* 打开文件的对象 */
        getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            Log.d(TAG, "用户选中了图片: " + uri);
            showImg(uri);
        });
        /* 请求权限的对象 */
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Log.d(TAG, "onCreate: 用户同意了申请的权限");
                getContentLauncher.launch("image/*");
            } else {
                PromptUtil.snackbarShowTxt(root, "没有权限，无法进行操作");
                Log.d(TAG, "onCreate: 用户拒绝了申请的权限");
            }
        });

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: 生命周期");

        binding = FragmentPersonalDetailBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        presenter = new PersonalDetailPresenter(this);
        /* 别忘了加上观察者，才能起作用啊！ */
        getLifecycle().addObserver(presenter);

        /* 监听OneInputFragment回传的值，修改昵称功能所用 */
        //todo:收到了数据，但是presenter重新查询导致覆盖了
        getParentFragmentManager().setFragmentResultListener(requestKey, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                Log.d(TAG, "onFragmentResult确实收到了数据:" + bundle);
                // We use a String here, but any type that can be put in a Bundle is supported
                String result = bundle.getString(dataKey);
                nicknameView.setText(result);
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: 生命周期");

        super.onViewCreated(view, savedInstanceState);

        final Toolbar toolbar = binding.toolbar;
        //绑定返回功能
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(root)
                .popBackStack());

        portraitView = binding.portrait;
        nicknameView = binding.nickname;
        emailView = binding.email;
        majorClassView = binding.majorClass;
        //学号
        principalView = binding.principal;
        roleView = binding.role;

        submitBtn = binding.editSubmitBtn;
        nicknameEdit = binding.editNickname;
        sexSwitch = binding.sex;
        portraitEdit = binding.editPortrait;

        navController = Navigation.findNavController(view);

        final MainActivityViewModel mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        User user = mainActivityViewModel.getUserInfo().getValue();

        nicknameEdit.setOnClickListener(v -> {
            openOneInputDialog(nicknameView, "输入昵称", previousUser.getNickname());
        });

        portraitEdit.setOnClickListener(v -> {
            openAlbum();
        });

        submitBtn.setOnClickListener(v -> {
            if (editInfo == null)
                editInfo = new ArrayMap<>();
            //避免重复点击
            submitBtn.setEnabled(false);

            /* id必须加上 */
            editInfo.put("id", String.valueOf(previousUser.getId()));

            /* 收集文字数据 */
            String currentNickname = nicknameView.getText().toString();
            boolean currentSex = sexSwitch.isChecked();
            if (!currentNickname.equals(previousUser.getNickname())) {
                editInfo.put("nickname", currentNickname);
            } else if (currentSex != previousUser.isSex()) {
                editInfo.put("sex", String.valueOf(currentSex));
            }

            /* 收集图片数据 */
            Single.create(emitter -> {
                if (this.portraitUri != null) {
                    File file = FileUtil.from(getContext(), this.portraitUri);
                    emitter.onSuccess(file);
                } else
                    emitter.onSuccess("null");
            }).subscribeOn(Schedulers.io())
                    //好像是必须在主线程绑定生命周期来着
                    .observeOn(AndroidSchedulers.mainThread())
                    .to(RxLifecycleUtils.bindLifecycle(this))
                    .subscribe(file -> {
                                Object thisFile = file;
                                if (thisFile instanceof String)
                                    thisFile = null;
                                presenter.saveEditInfo(repository, editInfo, (File) thisFile);
                            }, throwable -> {
                                throwable.printStackTrace();
                                submitBtn.setEnabled(true);
                                PromptUtil.snackbarShowTxt(root, "保存失败:" + throwable.getMessage());
                            }
                    );
        });

        //初始化数据
        presenter.initData(repository, user.getId());
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: 生命周期");
        super.onDestroyView();
        root = null;
        binding = null;
    }

    /**
     * 初始化控件内容
     *
     * @param user
     */
    private void initViewContent(final User user) {
        Glide.with(this)
                .load(GlideUtils.getImgDownloadUri(user.getPortrait()))
                .apply(GlideUtils.OPTIONS)
                .into(portraitView);
        nicknameView.setText(user.getNickname());
        emailView.setText(user.getEmail());
        majorClassView.setText(user.getMajorClass());
        principalView.setText(user.getCredentialNum());
        roleView.setText(user.getRole());
        sexSwitch.setChecked(user.isSex());
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
        portraitView.setImageURI(uri);
        portraitUri = uri;
    }

    /**
     * 打开仅包含一个输入框的弹窗，点击确定按钮关闭弹窗，并将输入传进view
     *
     * @param view         文本视图控件
     * @param title        弹窗标题
     * @param defaultInput 弹窗默认显示的内容
     */
    private void openOneInputDialog(TextView view, String title, String defaultInput) {
        OneInputDialog dialog = new OneInputDialog();
        dialog.setTitle(title);
        dialog.setDefaultInput(defaultInput);
        dialog.setListener(new OneInputDialog.OneInputDialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog, String result) {
                view.setText(result);
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                dialog.getDialog().cancel();
            }
        });
        dialog.show(getParentFragmentManager(), "PersonalDetailFragment_input");
    }

    /**
     * MVP模式网络查询用户资料
     *
     * @param user 要显示的用户资料
     */
    public void onInitSuccess(User user) {
        initViewContent(user);
        previousUser = user;
    }

    public void onInitFail() {
        Log.d(TAG, "onInitFail: 竟然发生了错误！");
    }

    /**
     * 图片上传失败
     * 或资料修改失败
     */
    public void onSavePersonalEditFail(String msg) {
        PromptUtil.snackbarShowTxt(root, msg);
        Log.d(TAG, "onSavePersonalEditFail: " + msg);
        submitBtn.setEnabled(true);
    }

    /**
     * 资料修改成功
     */
    public void onSavePersonalEditSuccess(List<String> data) {
        PromptUtil.snackbarShowTxt(root, "修改成功！");
        portraitUri = null;
        submitBtn.setEnabled(true);
        editInfo = null;
        if (data != null) {
            if (data.size() == 0)
                return;

            final MainActivityViewModel mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
            User user = mainActivityViewModel.getUserInfo().getValue();
            //修改头像
            if (data.get(0) != null)
                user.setPortrait(data.get(0));
            //修改昵称
            if (data.get(1) != null)
                user.setNickname(data.get(1));
        }
    }

}