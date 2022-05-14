package com.example.graduatedesign.student_activity_module.ui.qr_code;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.R;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.databinding.FragmentOneImageBinding;
import com.example.graduatedesign.student_activity_module.ui.RelativeStates;
import com.example.graduatedesign.utils.GlideUtils;
import com.example.graduatedesign.utils.PromptUtil;
import com.example.graduatedesign.utils.QRCodeUtil;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class QRCodeShowFragment extends Fragment {
    private static final String TAG = "QRCodeShowFragment";
    @Inject
    MyRepository repository;
    private View root;
    private FragmentOneImageBinding binding;
    private Bundle args;
    private QRCodePresenter presenter;
    private ImageView QRCodeView;

    public QRCodeShowFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOneImageBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        args = getArguments();

        QRCodeView = binding.imgQrCode;
        presenter = new QRCodePresenter(this);
        /* 别忘了加上观察者，才能起作用啊！ */
        getLifecycle().addObserver(presenter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);
        Bundle args = getArguments();
        if (args == null) {
            PromptUtil.snackbarShowTxt(root, "未接收到参数，无法生成二维码！");
            navController.popBackStack();
            return;
        }

        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        final TextView promptView = binding.prompt;
        final TextView titleView = binding.toolbarTitle;

        int type = args.getInt("type");
        String content = args.getString("content");
        String title = args.getString("title");
        String prompt = args.getString("prompt");

        titleView.setText(title);
        promptView.setText(prompt);

        String separator = QRCodeUtil.strSeparator;
        switch (type) {
            case RelativeStates.ActionPay:
                List<String> contentList = Arrays.asList(content.split(separator));
                try {
                    Integer userId = Integer.parseInt(contentList.get(0));
                    Integer activityId = Integer.parseInt(contentList.get(1));
                    presenter.getAlipayQrCodeBitMap(userId, repository, activityId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    onSignInfoFail("传入的参数非法，无法生成订单二维码！");
                }
                break;
            case RelativeStates.ActionCheckIn:
            case RelativeStates.ActionCheckOut:
                Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(content, 500, 500,
                        "UTF-8", "M", "20", Color.BLACK, Color.WHITE);
                QRCodeView.setImageBitmap(bitmap);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    public void onSignInfoSuccess(String path) {
        Glide.with(this)
                .load(GlideUtils.getImgDownloadUri(path))
                .apply(GlideUtils.OPTIONS)
                .into(QRCodeView);
    }

    public void onSignInfoFail(String msg) {
        PromptUtil.snackbarShowTxt(root, msg);
    }

}