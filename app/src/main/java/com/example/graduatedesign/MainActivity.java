package com.example.graduatedesign;

import android.content.Context;
import android.content.Intent;
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
import com.example.graduatedesign.net.netty.AppCache;
import com.example.graduatedesign.net.netty.PushService;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.ui.message.MessageViewModel;
import com.example.graduatedesign.utils.PromptUtil;
import com.example.graduatedesign.utils.RxLifecycleUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    //todo：顶级fragment添加滑动切换页面
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;
    private View root;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        root = binding.getRoot();
        setContentView(root);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        //获取本activity布局中的fragment显示容器-navHostFragment,再获取其navController
        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();

        //将navController和布局中的导航栏视图绑定
        NavigationUI.setupWithNavController(binding.navView, navController);
        //添加导航监听器
        hideOrShowNavigation(navController, binding.navView);

        //token登录时，要取出存储的用户信息
        viewModel.getTokenState().observe(this, tokenState -> {
            Log.d(TAG, "getTokenState: 观察");
            if (tokenState) {
                retrieveUserFromSP();
                startSocketService();
            }
        });
        //正常登录时，对用户信息改变的监听器
        viewModel.getUserInfo().observe(this, User1 -> {
            Log.d(TAG, "getUserInfo: 观察");
            //登录失败，或者登录失效，总之就是未登录状态
            if (User1 == null) {
                navigateToLogin();
                clearUserInSP();
            }
            //登录成功专属
            else {
                navigateToHome();
                startSocketService();
            }
        });

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        root = null;
        binding = null;

        //todo：实际上service可能应该存在更久
        PushService service = AppCache.getService();
        if (service != null) {
            AppCache.setMyInfo(null);
            /* 关闭socket服务 */
            service.stopSelf();
        }

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
        user.setPortrait(sp.getString("portrait", null));
        user.setCredentialInfoId(sp.getInt("credentialInfoId", -1));
        user.setSchoolId(sp.getInt("schoolId", -1));
        user.setAssociationAdmin(sp.getBoolean("isAssociationAdmin", false));

        viewModel.getUserInfo().setValue(user);
    }

    /**
     * 清除 SharedPreferences 中缓存的用户信息
     */
    private void clearUserInSP() {
        getSharedPreferences("userInfo", Context.MODE_PRIVATE).edit().clear().commit();
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

    private void startSocketService() {
        /* 启动socket服务 */
        Log.d(TAG, "启动socket服务");
        PushService.serviceStartCallBack = (code, msg, unused) -> {
            socketLogin();
        };
        startService(new Intent(getApplicationContext(), PushService.class));
    }


    /**
     * 登录成功后，取出登录用户的信息，查询后端接口，获取未读信息
     */
    @Deprecated
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

    /**
     * socket登录
     */
    private void socketLogin() {
        if (AppCache.getMyInfo() == null || AppCache.getMyInfo().getToken() == null) {
            SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            String token = sp.getString("token", null);

            Completable.create(emitter -> {
                /* socket登录 */
                Log.d(TAG, "storeLoggedInUser: 登录中");
                AppCache.getService().login(token, (code, msg, unused) -> {
                    Log.d(TAG, "登录结果: " + msg);
                });
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .to(RxLifecycleUtils.bindLifecycle(root)).subscribe(() -> {
                    },
                    throwable -> throwable.printStackTrace());
        }
    }

}