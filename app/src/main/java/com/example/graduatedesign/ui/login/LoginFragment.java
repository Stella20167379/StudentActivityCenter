package com.example.graduatedesign.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.data.model.Result;
import com.example.graduatedesign.databinding.FragmentLoginBinding;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.utils.CountDownTimerUtil;
import com.example.graduatedesign.utils.PromptUtil;
import com.google.android.material.tabs.TabLayout;

import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private static final String TAG = "LoginFragment";
    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;
    private View root;

    private EditText principalEditText;
    private EditText credentialEditText;
    private EditText verifyCodeEditText;
    private Button getVerifyCodeBtn;

    private CountDownTimerUtil timer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        principalEditText = binding.principal;
        credentialEditText = binding.credential;
        verifyCodeEditText = binding.verifyCode;
        getVerifyCodeBtn = binding.btnGetVerifyCode;

        return root;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final TextView txtToRegister = binding.txtToRegister;
        final TextView txtForgetPass = binding.txtForgetPass;
        final Button loginButton = binding.btnLogin;
        final ProgressBar loadingProgressBar = binding.loading;
        final TabLayout tabLayout = binding.tabLayout;
        final NavController navController = Navigation.findNavController(root);

        //设置跳转至注册页面/忘记密码页面的文字点击事件
        SpannableString registerSpan = new SpannableString("没有账号？去注册");
        SpannableString forgetPassSpan = new SpannableString("忘记密码？");

        //设置点击事件
        registerSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                navController.navigate(R.id.action_loginFragment_to_registerFirstFragment);
            }
        }, 0, registerSpan.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        forgetPassSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                navController.navigate(R.id.action_navigation_login_to_forgetPassFirstFragment);
            }
        }, 0, forgetPassSpan.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        //使用ClickableSpan的文本如果想真正实现点击作用，必须为TextView设置setMovementMethod方法
        // 否则没有点击相应，至于setHighlightColor方法则是控制点击是的背景色。
        txtToRegister.setMovementMethod(LinkMovementMethod.getInstance());
        txtForgetPass.setMovementMethod(LinkMovementMethod.getInstance());

        txtToRegister.setText(registerSpan);
        txtForgetPass.setText(forgetPassSpan);

        //设置登录类型监听器
        tabLayout.addOnTabSelectedListener(this);



        /* ----------------- 和loginViewModel的数据双向绑定 START ------------------- */

        /* 数据变化 -> 显示提示消息回调，停止验证码按钮计时器*/

        loginViewModel.getVerifyCodeError().observe(getViewLifecycleOwner(), value -> {
            if (value == null)
                return;
            PromptUtil.snackbarShowTxt(root, value);
            timer.cancelTimerCount();
        });

        /* 观察数据状态，将ViewModel数据绑定至视图，由ViewModel调用回调处理，数据更新 -> 更新视图 */

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), loginFormState -> {
            //用户未进行输入时
            if (loginFormState == null) {
                return;
            }
            //根据用户输入是否合法设置登录按钮的状态
            loginButton.setEnabled(loginFormState.isDataValid());
            //用户输入不合法，显示错误信息
            if (loginFormState.getPrincipalError() != null) {
                principalEditText.setError(getString(loginFormState.getPrincipalError()));
            }
            if (loginFormState.getCredentialError() != null) {
                credentialEditText.setError(getString(loginFormState.getCredentialError()));
            }
        });

        /* 同上，数据更新 -> 视图更新，登录验证后的视图处理 */

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            //登录成功
            if (loginResult instanceof Result.Success) {
                OnLoginSuccess((Map<String, Object>) ((Result.Success<?>) loginResult).getData());
            }
            //登录失败
            else {
                onLoginFailed(loginResult.toString());
            }
        });

        /* 监听到输入变化后，调用ViewModel方法传递数据更改，将视图绑定至ViewModel数据，视图更新 -> 数据更新 */
        //EditableText类型视图组件的监听器
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
                loginViewModel.onLoginFormChanged(principalEditText.getText().toString(),
                        credentialEditText.getText().toString(),
                        verifyCodeEditText.getText().toString());
            }
        };
        //绑定视图监听器
        principalEditText.addTextChangedListener(afterTextChangedListener);
        credentialEditText.addTextChangedListener(afterTextChangedListener);
        verifyCodeEditText.addTextChangedListener(afterTextChangedListener);


        /* 登录按钮点击监听，点击按钮调用ViewModel方法，实现 视图操作-> 数据更新 */

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(principalEditText.getText().toString(),
                    credentialEditText.getText().toString());
        });

        /* 获取验证码按钮点击事件,结果影响数据，视图操作 -> 数据更新 */

        getVerifyCodeBtn.setOnClickListener(v -> {
            LoginFormState formState = loginViewModel.getLoginFormState().getValue();
            if (formState == null) {
                verifyCodeEditText.setError("请输入有效邮箱");
                return;
            }

            if (formState.getCredentialError() != null) {
                return;
            }

            //开始倒计时，总时长60 000毫秒，即60秒，间隔1秒
            timer = new CountDownTimerUtil(getVerifyCodeBtn, 60000, 1000);
            timer.start();
            loginViewModel.getLoginVerifyCode(principalEditText.getText().toString());
        });


        /* ----------------------------- END ------------------------------- */
    }


    /**
     * 登录成功的回调操作
     * @param data 登录成功后服务器回传的数据
     */
    private void OnLoginSuccess(@NonNull Map<String, Object> data) {
        MainActivityViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        //更新应用的viewModel，会触发activity的跳转事件
        viewModel.getUserInfo().setValue(storeLoggedInUser(data));
    }

    /**
     * 将数据存入SharedPreferences
     * @param data 登录成功后服务器回传的数据
     * @return
     */
    private User storeLoggedInUser(Map<String, Object> data){

        User user = (User) data.get("LoggedInUser");
        String tokenName = (String) data.get("tokenName");
        String token = (String) data.get("token");

        assert user != null;
        //TODO:可以考虑序列化存储
        //更新SharedPreferences
        SharedPreferences sp = this.requireActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("id", user.getId());
        editor.putString("schoolName", user.getSchoolName());
        editor.putString("nickname", user.getNickname());
        editor.putString("email", user.getEmail());
        editor.putString("portrait", user.getPortrait());
        editor.putInt("credentialInfoId", user.getCredentialInfoId());
        editor.putInt("schoolId", user.getSchoolId());
        editor.putString("realName", user.getRealName());
        editor.putBoolean("sex", user.isSex());
        editor.putString("credentialNum", user.getCredentialNum());
        editor.putString("role", user.getRole());
        editor.putString("majorClass", user.getMajorClass());
        editor.putBoolean("isAssociationAdmin", user.isAssociationAdmin());

        editor.putString("tokenName", tokenName);
        editor.putString("token", token);
        editor.apply();

        return user;
    }

    /**
     * 登录失败的回调操作
     * @param errorString 提示信息
     */
    private void onLoginFailed(String errorString) {
        PromptUtil.snackbarShowTxt(root, errorString);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //position从0开始
        switchLoginView(tab.getPosition() + 1);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    /**
     * 根据tabLayout点击事件切换登录框样式
     *
     * @param loginType 根据tab视图选中的标签位置得到的登录类型
     */
    private void switchLoginView(int loginType) {
        loginViewModel.getLoginType().setValue(loginType);
        //密码登录
        if (loginType == 1) {
            getVerifyCodeBtn.setVisibility(View.GONE);
            verifyCodeEditText.setVisibility(View.GONE);

            principalEditText.setHint(R.string.prompt_account);
            credentialEditText.setVisibility(View.VISIBLE);
        }
        //邮箱验证码登录
        else {
            getVerifyCodeBtn.setVisibility(View.VISIBLE);
            verifyCodeEditText.setVisibility(View.VISIBLE);

            principalEditText.setHint(R.string.prompt_email);
            credentialEditText.setVisibility(View.GONE);

        }
    }
}

