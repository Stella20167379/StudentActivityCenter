package com.example.graduatedesign.association_module.ui.edit;

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
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.R;
import com.example.graduatedesign.association_module.data.Association;
import com.example.graduatedesign.association_module.ui.AssociationViewModel;
import com.example.graduatedesign.databinding.FragmentAssociationEditBinding;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GlideUtils;
import com.example.graduatedesign.utils.PromptUtil;

import java.util.HashMap;
import java.util.Map;


public class AssociationEditFragment extends Fragment {
    private static final String TAG = "AssociationEditFragment";
    private FragmentAssociationEditBinding binding;
    private View root;
    private AssociationViewModel associationViewModel;

    private ImageView portraitView;

    /* 此为修改社团名称返回的数据监听key */
    private String requestKey = "AssociationEditFragment";
    /* 请求权限的对象 */
    private ActivityResultLauncher<String> requestPermissionLauncher;
    /* 打开文件的对象 */
    private ActivityResultLauncher<String> getContentLauncher;
    /* 用户选中的图片uri */
    private Uri portraitUri = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAssociationEditBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        associationViewModel = new ViewModelProvider(requireActivity()).get(AssociationViewModel.class);
        portraitView = binding.portrait;

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
        final NavController navController = Navigation.findNavController(root);
        Association previousAssociation = associationViewModel.getAssociationToShowDetail().getValue();

        if (previousAssociation == null) {
            PromptUtil.snackbarShowTxt(root, "没有找到社团详情信息！！");
            navController.popBackStack();
            return;
        }

        final TextView associationName = binding.associationName;
        final TextView establishTime = binding.establishTime;
        final TextView introduction = binding.introduction;
        final ImageView editPortrait = binding.editPortrait;
        final ImageView editAssociationName = binding.editAssociationName;
        final Button editSubmitBtn = binding.editSubmitBtn;
        final Toolbar toolbar = binding.toolbar;

        toolbar.setNavigationOnClickListener(v -> {
            navController.popBackStack();
        });

        /* 监听OneInputFragment回传的值 */
        getParentFragmentManager().setFragmentResultListener(requestKey, this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            String result = bundle.getString("associationName");
            associationName.setText(result);
        });

        /* 保存信息操作结果提示 */
        associationViewModel.getSaveInfoPrompt().observe(getViewLifecycleOwner(), prompt -> PromptUtil.snackbarShowTxt(view, prompt));

        //初始化设置控件显示内容
        Glide.with(this)
                .load(DataUtil.getImgDownloadUri(previousAssociation.getCover()))
                .apply(GlideUtils.OPTIONS)
                .into(portraitView);
        associationName.setText(previousAssociation.getAssociationName());
        establishTime.setText(previousAssociation.getEstablishTime());
        introduction.setText(previousAssociation.getIntroduction());

        editAssociationName.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", "修改社团名称");
            bundle.putString("default", previousAssociation.getAssociationName());
            bundle.putString("dataKey", "associationName");
            bundle.putString("requestKey", requestKey);
            Navigation.findNavController(view).navigate(R.id.oneInputFragment, bundle);
        });

        editPortrait.setOnClickListener(v -> {
            openAlbum();
        });

        editSubmitBtn.setOnClickListener(v -> {
            if (portraitUri != null)
                associationViewModel.uploadImg(portraitUri);

            Map<String, Object> editInfo = new HashMap<>();
            String currentName = associationName.getText().toString();
            String currentIntroduction = introduction.getText().toString();

            if (!currentName.equals(previousAssociation.getAssociationName())) {
                editInfo.put("associationName", currentName);
            } else if (!currentIntroduction.equals(previousAssociation.getIntroduction())) {
                editInfo.put("introduction", currentIntroduction);
            } else {
                editInfo = null;
            }
            associationViewModel.saveEditInfo(editInfo);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
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


}