package com.example.graduatedesign.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.graduatedesign.R;
import com.example.graduatedesign.data.model.Result;
import com.example.graduatedesign.databinding.FragmentRegisterSecondBinding;
import com.example.graduatedesign.utils.CountDownTimerUtil;
import com.example.graduatedesign.utils.PromptUtil;

import java.util.HashMap;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterSecondFragment extends Fragment {
    private FragmentRegisterSecondBinding binding;
    private View root;
    private RegisterViewModel viewModel;
    private Bundle args;
    private CountDownTimerUtil timer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterSecondBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        args = getArguments();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button registerBtn = binding.btnRegister;
        final Button getVerifyCodeBtn = binding.btnGetVerifyCode;
        final EditText emailView = binding.email;
        final EditText nicknameView = binding.nickname;
        final EditText passView = binding.credential;
        final EditText verifyCodeView = binding.verifyCode;
        final ProgressBar progressBar = binding.progressBar;

        initBackToolBar();

        /* ------------------- 绑定ViewModel中数据与页面中视图控件 ----------------- */
        /* 以下是实现数据改变->视图改变 */
        viewModel.getRegisterFormState().observe(getViewLifecycleOwner(), registerFormState -> {
            if (registerFormState == null)
                return;
            registerBtn.setEnabled(registerFormState.getValid());
            if (registerFormState.getEmailError() != null)
                emailView.setError(registerFormState.getEmailError());
            if (registerFormState.getPassError() != null)
                passView.setError(registerFormState.getPassError());
            if (registerFormState.getVerifyCodeError() != null)
                verifyCodeView.setError(registerFormState.getVerifyCodeError());
            if (registerFormState.getNicknameError() != null)
                nicknameView.setError(registerFormState.getNicknameError());
        });

        viewModel.getRegisterResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            progressBar.setVisibility(View.GONE);
            if (result instanceof Result.Success) {
                onRegisterSuccess();
            } else {
                onRegisterFail(result.toString());
            }
        });

        viewModel.getVerifySendError().observe(getViewLifecycleOwner(), s -> {
            PromptUtil.snackbarShowTxt(root, s);
            if (timer != null)
                timer.cancelTimerCount();
        });

        /* 以下是实现视图改变->数据改变 */
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.onRegisterDataChanged(emailView.getText().toString(), nicknameView.getText().toString(),
                        passView.getText().toString(), verifyCodeView.getText().toString());
            }
        };
        emailView.addTextChangedListener(afterTextChangedListener);
        nicknameView.addTextChangedListener(afterTextChangedListener);
        passView.addTextChangedListener(afterTextChangedListener);
        verifyCodeView.addTextChangedListener(afterTextChangedListener);

        registerBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);

            Integer collegeId = args.getInt("collegeId");
            int credentialInfoId = args.getInt("credentialInfoId");
            String verifyCode = verifyCodeView.getText().toString();
            String nickname = nicknameView.getText().toString();
            String pass = passView.getText().toString();
            String email = emailView.getText().toString();

            Map<String, Object> registerData = new HashMap<>();
            registerData.put("verifyCode", verifyCode);
            registerData.put("collegeId", collegeId);
            registerData.put("credentialInfoId", credentialInfoId);
            registerData.put("nickname", nickname);
            registerData.put("pass", pass);
            registerData.put("email", email);

            viewModel.register(registerData);

        });

        getVerifyCodeBtn.setOnClickListener(v -> {
            RegisterFormState formState = viewModel.getRegisterFormState().getValue();
            if (formState == null) {
                emailView.setError("请输入有效邮箱");
                return;
            }

            if (formState.getEmailError() != null) {
                return;
            }

            //开始倒计时，总时长60 000毫秒，即60秒，间隔1秒
            timer = new CountDownTimerUtil(getVerifyCodeBtn, 60000, 1000);
            timer.start();

            viewModel.getRegisterVerifyCode(emailView.getText().toString());
        });

        /* ------------------------------- END ------------------------------ */
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    private void onRegisterSuccess() {
        PromptUtil.snackbarShowTxt(root, "注册成功，返回登录页面！");

        NavController navController = Navigation.findNavController(root);
        NavOptions.Builder builder = new NavOptions.Builder();
        builder.setPopUpTo(R.id.mobile_navigation, false);
        NavOptions options = builder.build();
        navController.navigate(R.id.navigation_login, null, options);
    }

    private void onRegisterFail(String msg) {
        PromptUtil.snackbarShowTxt(root, msg);
    }

    /**
     * 给头顶工具栏添加事件
     */
    private void initBackToolBar(){
        /* 在onCreateView中找不到NavController */
        Toolbar toolbar=binding.toolbar;
        toolbar.setNavigationOnClickListener(v-> {
            final NavController navController= Navigation.findNavController(root);
            navController.popBackStack();
        });
    }
}