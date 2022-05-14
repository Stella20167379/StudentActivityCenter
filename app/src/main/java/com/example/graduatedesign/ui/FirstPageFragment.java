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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root=null;
        binding = null;
    }
}