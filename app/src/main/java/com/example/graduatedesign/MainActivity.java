package com.example.graduatedesign;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.graduatedesign.databinding.ActivityMainBinding;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.ui.message.MessageViewModel;
import com.example.graduatedesign.utils.PromptUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;
    private View root;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        root = binding.getRoot();
        setContentView(root);

        //获取本activity布局中的fragment显示容器-navHostFragment,再获取其navController
        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();

        //将navController和布局中的导航栏视图绑定
        NavigationUI.setupWithNavController(binding.navView, navController);
        //添加导航监听器
        hideOrShowNavigation(navController, binding.navView);

    }

    @Override
    protected void onStart() {
        super.onStart();

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        //token登录时，要取出存储的用户信息
        viewModel.getTokenState().observe(this, tokenState -> {
            if (tokenState)
                retrieveUserFromSP();
        });
        //正常登录时，对用户信息改变的监听器
        viewModel.getUserInfo().observe(this, User1 -> {
            //登录失败，或者登录失效，总之就是未登录状态
            if (User1 == null) {
                navigateToLogin();
            }
            //登录成功专属
            else {
                navigateToHome();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        root = null;
        binding = null;
    }

    /**
     * 如果不属于最顶级导航，则隐藏导航栏
     *
     * @param navController        要添加监听器的目的导航控制器
     * @param bottomNavigationView 要隐藏的底部导航视图组件
     */
    private void hideOrShowNavigation(NavController navController, BottomNavigationView bottomNavigationView) {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            boolean showAppBottomNav = false;
            if (arguments != null) {
                showAppBottomNav = arguments.getBoolean("showAppBottomNav", false);
            }
            if (showAppBottomNav) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            } else {
                bottomNavigationView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 用以判断是否登录后的跳转页面操作，目的地要么是登录页面要么是首页
     * 参考 https://blog.csdn.net/vitaviva/article/details/109881582
     *
     * @param navController 导航控制器
     */
    private void switchFragment(NavController navController, int destinationId) {
        navController.getGraph().setStartDestination(destinationId);

        //清空回退栈至最底部的mobile_navigation导航图
        NavOptions.Builder builder = new NavOptions.Builder();
        builder.setPopUpTo(R.id.mobile_navigation, false);
        NavOptions options = builder.build();

        navController.navigate(destinationId, null, options);
    }

    /**
     * token有效，取出SharedPreferences中存储的用户信息，放入内存缓存
     */
    private void retrieveUserFromSP() {
        SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        User user = new User();
        user.setId(sp.getInt("id", -1));
        user.setSchoolName(sp.getString("schoolName", null));
        user.setNickname(sp.getString("nickname", null));
        user.setEmail(sp.getString("email", null));
        user.setPortrait(sp.getString("portrait", null));
        user.setCredentialNum(sp.getString("credentialNum", null));
        user.setCredentialInfoId(sp.getInt("credentialInfoId", -1));
        user.setSchoolId(sp.getInt("schoolId", -1));
        user.setRealName(sp.getString("realName", null));
        user.setSex(sp.getBoolean("sex", false));
        user.setRole(sp.getString("role", null));
        user.setMajorClass(sp.getString("majorClass", null));
        user.setAssociationAdmin(sp.getBoolean("isAssociationAdmin", false));

        viewModel.getUserInfo().setValue(user);
    }

    /**
     * 登录失败，或登录状态失效，跳转至登录页面
     */
    private void navigateToLogin() {
        String prompt = viewModel.getLoginPrompt();
        if (prompt != null) {
            new AlertDialog.Builder(this)
                    .setMessage(prompt)
                    .setPositiveButton("确定", (dialogInterface, i) -> {
                        switchFragment(navController, R.id.navigation_login);
                    })
                    .create()
                    .show();
        } else {
            Log.d(TAG, "onCreate: 没有登录提示，所以没有弹框");
            switchFragment(navController, R.id.navigation_login);
        }
    }

    /**
     * 登录成功，或在登录状态，跳转至首页，同时进行信息的一些初始化工作
     */
    private void navigateToHome() {
        //跳转页面
        switchFragment(navController, R.id.navigation_home);
        //请求后端，查询未读信息
//        checkUnreadMessages();
    }

    /**
     * 登录成功后，取出登录用户的信息，查询后端接口，获取未读信息
     */
    private void checkUnreadMessages() {
        User user = viewModel.getUserInfo().getValue();
        if (user == null) {
            PromptUtil.snackbarShowTxt(root, "网络错误");
            return;
        }
        Integer userId = user.getId();
        MessageViewModel messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        messageViewModel.initMessageData(userId, this);
    }

}