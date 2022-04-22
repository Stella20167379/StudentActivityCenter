package com.example.graduatedesign.ui.login;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.data.LoginRepository;
import com.example.graduatedesign.databinding.FragmentForgetPassFirstBinding;
import com.example.graduatedesign.presenter.ForgetPassFirstPresenter;
import com.example.graduatedesign.utils.CountDownTimerUtil;
import com.example.graduatedesign.utils.PromptUtil;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ForgetPassFirstFragment extends Fragment {

    @Inject
    LoginRepository repository;

    private FragmentForgetPassFirstBinding binding;
    private View root;
    private ForgetPassFirstPresenter presenter;
    private CountDownTimerUtil timer;

    private Button getVerifyCodeBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForgetPassFirstBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        presenter = new ForgetPassFirstPresenter(this);

        getVerifyCodeBtn = binding.btnGetVerifyCode;

        Toolbar toolbar = binding.toolbar;
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(root).popBackStack());
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button btnNext = binding.btnToResetPass;
        final EditText emailView = binding.editEmail;
        final EditText verifyCodeView = binding.verifyCode;
        final EditText newPassView= binding.newPassword;

        //获取验证码按钮点击事件
        getVerifyCodeBtn.setOnClickListener(v -> {
            String email = emailView.getText().toString();
            if (isEmailInvalid(email)) {
                emailView.setError("请输入合法的邮箱地址");
            } else {
                presenter.getVerifyCode(email,repository);
                timer=new CountDownTimerUtil(getVerifyCodeBtn, 60000, 1000);
                timer.start();
            }
        });

        //确定按钮点击事件
        btnNext.setOnClickListener(v -> {
            String email = emailView.getText().toString();
            String verifyCode = verifyCodeView.getText().toString();
            String newPass=newPassView.getText().toString();

            if (isEmailInvalid(email))
                emailView.setError("请输入合法的邮箱地址");
            else if (verifyCode.trim().isEmpty() || TextUtils.isEmpty(verifyCode))
                verifyCodeView.setError("验证码不能为空");
            else {
                Map<String,String> resetData=new HashMap();
                resetData.put("email",email);
                resetData.put("verifyCode",verifyCode);
                resetData.put("newPass",newPass);

                presenter.resetPass(resetData,repository);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
        presenter = null;
    }

    private boolean isEmailInvalid(String email) {
        return email.trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void onGetVerifyCodeFail(String msg){
        PromptUtil.snackbarShowTxt(root,msg);

        timer.cancelTimerCount();
    }

    public void onSuccessReset() {
        PromptUtil.snackbarShowTxt(root,R.string.prompt_reset_pass_success);
        //若用户在登录状态，重置密码后用户需重新登录，否则退回登录页面
        MainActivityViewModel activityViewModel=new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        if (activityViewModel.getUserInfo().getValue()!=null){
            activityViewModel.setLoginPrompt(getString(R.string.prompt_login_for_reset_pass));
            activityViewModel.getUserInfo().setValue(null);
        }else {
            Navigation.findNavController(root).popBackStack();
        }
    }

    public void onFailReset(String msg) {
        PromptUtil.snackbarShowTxt(root, msg);
    }
}