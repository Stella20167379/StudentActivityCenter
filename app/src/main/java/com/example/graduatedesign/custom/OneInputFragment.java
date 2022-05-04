package com.example.graduatedesign.custom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.graduatedesign.databinding.FragmentWithOneInputBinding;

public class OneInputFragment extends Fragment {
    private FragmentWithOneInputBinding binding;
    private View root;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWithOneInputBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView title = binding.toolbarTitle;
        final Toolbar toolbar = binding.toolbar;
        final EditText input = binding.inputTxt;
        final Button confirmBtn = binding.confirmBtn;
        final Bundle args = getArguments();
        final NavController navController = Navigation.findNavController(root);

        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
        title.setText(args.getString("title"));
        input.setText(args.getString("default"));
        String dataKey = args.getString("dataKey");
        String requestKey = args.getString("requestKey");

        confirmBtn.setOnClickListener(v -> {
            Bundle result = new Bundle();
            //使用启动本fragment时传入的数据作为返回数据的key
            result.putString(dataKey, input.getText().toString());
            //标识这是本fragment返回的信息
            getParentFragmentManager().setFragmentResult(requestKey, result);
            navController.popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }
}
