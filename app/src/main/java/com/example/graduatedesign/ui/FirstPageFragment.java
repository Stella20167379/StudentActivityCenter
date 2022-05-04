package com.example.graduatedesign.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.databinding.FragmentFirstPageBinding;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class FirstPageFragment extends Fragment {
    private FragmentFirstPageBinding binding;
    private static final String TAG = "FirstPageFragment";
    private MainActivityViewModel viewModel;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFirstPageBinding.inflate(inflater, container, false);
        root=binding.getRoot();
        viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //打开应用时，viewModel保存信息默认值为空，要手动触发监听器事件
         if (viewModel.getUserInfo().getValue() == null) {
         SharedPreferences sp = requireActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
         String token = sp.getString("token", null);
             String tokenName = sp.getString("tokenName", null);
         viewModel.checkLoginState(tokenName,token);
         }

        /**
         final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
         alertBuilder.setTitle("修改某某某的弹窗");
         //设置不能通过点击别处取消
         alertBuilder.setCancelable(false);

         final EditText alertEditTxt = new EditText(getContext());
         alertEditTxt.setMaxLines(1);
         alertEditTxt.setTextColor(Color.BLACK);

         alertBuilder.setView(alertEditTxt);
         AlertDialog dialog=alertBuilder.setPositiveButton("确定", (dialog01, which) -> {
         String txt = alertEditTxt.getText().toString();
         Toast.makeText(getContext(), txt, Toast.LENGTH_SHORT).show();
         }).setNegativeButton("取消", null)
         .create();
         */

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root=null;
        binding = null;
    }



    /**
     * 等待2秒进行跳转
     */
    public void waitToNavigate() {
        Completable.create(emitter -> {

        }).delay(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(RxLifecycleUtils.bindLifecycle(this))
                .subscribe();

    }

}