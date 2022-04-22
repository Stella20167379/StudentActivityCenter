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
import com.example.graduatedesign.ui.message.MessageViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;
    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        root = binding.getRoot();
        setContentView(root);

        //获取本activity布局中的fragment显示容器-navHostFragment,再获取其navController
        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        final NavController navController = navHostFragment.getNavController();

        //将navController和布局中的导航栏视图绑定
        NavigationUI.setupWithNavController(binding.navView, navController);
        //添加导航监听器
        hideOrShowNavigation(navController, binding.navView);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        /* 可能是由于生命周期原因(也有可能是地方放错了，总之懒得改了)，以下两个监听回调若分开放在onCreate+onStart，会造成死锁，控制台打印了两遍跳转页面时的log */
        //启动应用时，验证token状态的回调
        viewModel.getTokenState().observe(this, state -> {
            if (state == null)
                return;
            //token验证结果为有效
            if (state) {
                //取出SharedPreferences中存储的用户信息，放入内存备用
                //尝试获取已登录保存过的用户信息
                SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                int userId = sp.getInt("userId", -1);
                String schoolName = sp.getString("schoolName", null);
                String email = sp.getString("email", null);
                int credentialInfoId = sp.getInt("credentialInfoId", -1);
                String portrait = sp.getString("portrait", null);

                LoggedInUser loggedInUser = new LoggedInUser();
                loggedInUser.setUserId(userId);
                loggedInUser.setEmail(email);
                loggedInUser.setPortrait(portrait);
                loggedInUser.setCredentialInfoId(credentialInfoId);
                loggedInUser.setSchoolName(schoolName);

                //触发登录成功，跳转首页的事件
                viewModel.getUserInfo().setValue(loggedInUser);

            }
        });

        //用户信息改变时的监听器
        viewModel.getUserInfo().observe(this, loggedInUser1 -> {
            //登录失败，或者登录失效，总之就是未登录状态
            if (loggedInUser1 == null) {

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
            //登录成功专属
            else {
                //跳转页面
                switchFragment(navController, R.id.navigation_home);
                //请求后端，查询未读信息存入数据库
                checkUnreadMessages();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //打开应用时，viewModel保存信息默认值为空，要手动触发监听器事件
        if (viewModel.getUserInfo().getValue() == null) {
            SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            String token = sp.getString("token", null);
            String refreshToken = sp.getString("refreshToken", null);

            viewModel.checkLoginState(token, refreshToken);

        }
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
     * 登录成功后，取出登录用户的信息，查询后端接口，获取未读信息用以更新数据库
     */
    private void checkUnreadMessages() {
        LoggedInUser user = viewModel.getUserInfo().getValue();
        if (user == null)
            throw new IllegalStateException("登录成功跳转页面，却没有获得对应的用户信息！");
        Integer userId = user.getUserId();
        MessageViewModel messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        messageViewModel.initMessageData(userId,this);
    }

}