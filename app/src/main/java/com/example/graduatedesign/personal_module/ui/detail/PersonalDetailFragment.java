package com.example.graduatedesign.personal_module.ui.detail;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.databinding.FragmentPersonalDetailBinding;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GlideUtils;
import com.example.graduatedesign.utils.PromptUtil;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

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
    private TextView realNameView;
    private TextView roleView;

    private Button submitBtn;
    private ImageView nicknameEdit;
    private SwitchMaterial sexSwitch;
    private ImageView portraitEdit;

    /* 此为进入页面时MVP查询到的数据 */
    private User previousUser;
    /* 此为修改昵称返回的数据监听key */
    private String requestKey="PersonalDetailFragment";
    /* 请求权限的对象 */
    private ActivityResultLauncher<String> requestPermissionLauncher;
    /* 打开文件的对象 */
    private ActivityResultLauncher<String> getContentLauncher;
    /* 用户选中的图片uri */
    private Uri portraitUri=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonalDetailBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        presenter=new PersonalDetailPresenter(this);

        /* 监听OneInputFragment回传的值，修改昵称功能所用 */
        getParentFragmentManager().setFragmentResultListener(requestKey, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                String result = bundle.getString("nickname");
                nicknameView.setText(result);
            }
        });

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

        portraitView = binding.portrait;
        nicknameView = binding.nickname;
        emailView = binding.email;
        majorClassView = binding.majorClass;
        //学号
        principalView = binding.principal;
        realNameView = binding.realName;
        roleView = binding.role;

        submitBtn = binding.editSubmitBtn;
        nicknameEdit = binding.editNickname;
        sexSwitch = binding.sex;
        portraitEdit = binding.editPortrait;

        submitBtn.setOnClickListener(v -> {
            if (portraitUri!=null)
                presenter.uploadImg(repository,portraitUri);

            Map<String,Object> editInfo=new HashMap<>();
            String currentNickname=nicknameView.getText().toString();
            boolean currentSex=sexSwitch.isChecked();

            if (!currentNickname.equals(previousUser.getNickname())){
                editInfo.put("nickname",currentNickname);
            }else if (currentSex!=previousUser.isSex()){
                editInfo.put("sex",currentSex);
            }else {
                editInfo=null;
            }
            presenter.saveEditInfo(repository,editInfo);
        });

        nicknameEdit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", "修改昵称");
            bundle.putString("default",previousUser.getNickname());
            bundle.putString("dataKey","nickname");
            bundle.putString("requestKey",requestKey);
            Navigation.findNavController(view).navigate(R.id.oneInputFragment, bundle);
        });

        portraitEdit.setOnClickListener(v -> {
            openAlbum();
        });

        //初始化数据
        final MainActivityViewModel mainActivityViewModel=new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        User user =mainActivityViewModel.getUserInfo().getValue();
        presenter.initData(repository,user.getId());
    }

    @Override
    public void onDestroyView() {
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
                .load(DataUtil.getImgDownloadUri(DataUtil.getImgDownloadUri(user.getPortrait())))
                .apply(GlideUtils.OPTIONS)
                .into(portraitView);
        nicknameView.setText(user.getNickname());
        emailView.setText(user.getEmail());
        majorClassView.setText(user.getMajorClass());
        principalView.setText(user.getCredentialNum());
        realNameView.setText(user.getRealName());
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
        portraitUri=uri;
    }


    /**
     * MVP模式网络查询用户资料
     * @param user 要显示的用户资料
     */
    public void onInitSuccess(User user){
        initViewContent(user);
        previousUser=user;
    }

    public void onInitFail(){
        Log.d(TAG, "onInitFail: 竟然发生了错误！");
    }

    /**
     * 图片上传失败
     * 或资料修改失败
     */
    public void onEditPersonalInfoFail(String msg){
        PromptUtil.snackbarShowTxt(root,msg);
    }

    /**
     * 资料修改成功
     */
    public void onEditPersonalInfoSuccess(){
        PromptUtil.snackbarShowTxt(root,"修改成功！");

    }

    /**
     * 图片上传成功的回调
     */
    public void onUploadImgSuccess(){
        portraitUri=null;
    }

}