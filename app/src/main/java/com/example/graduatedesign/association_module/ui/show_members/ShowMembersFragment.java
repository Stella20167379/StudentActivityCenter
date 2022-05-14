package com.example.graduatedesign.association_module.ui.show_members;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.association_module.adapter.ShowMembersAdapter;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.databinding.FragmentWithOneRecyclerviewBinding;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.utils.PromptUtil;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShowMembersFragment extends Fragment {
    private FragmentWithOneRecyclerviewBinding binding;
    private View root;
    private RecyclerView recyclerView;
    private ShowMembersPresenter presenter;
    private ShowMembersAdapter adapter;
    private Bundle args;
    private ProgressBar progressBar;

    @Inject
    MyRepository repository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWithOneRecyclerviewBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        presenter = new ShowMembersPresenter(this);
        /* 别忘了加上观察者，才能起作用啊！ */
        getLifecycle().addObserver(presenter);
        args = getArguments();
        progressBar = binding.progressBar;
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView title = binding.toolbarTitle;
        final Toolbar toolbar = binding.toolbar;
        recyclerView = binding.recyclerView;
        adapter = new ShowMembersAdapter();
        //绑定返回功能
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(root)
                .popBackStack());
        title.setText("社团成员");

        int associationId = args.getInt("associationId");
        if (associationId == 0)
            PromptUtil.showAlert(getContext(), "参数错误，请返回");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(new AuthorizeMemberListener());

        presenter.initData(repository, associationId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
        presenter = null;
    }

    /**
     * 调用授权方法向服务器发起请求
     *
     * @param memberId 授权目标成员id
     */
    private void authorizeMember(int memberId) {
        MainActivityViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        int adminId = viewModel.getUserInfo().getValue().getId();
        presenter.authorizeMember(args.getInt("associationId"), adminId, repository, memberId);
    }

    public void onInitSuccess(List<User> data) {
        progressBar.setVisibility(View.GONE);
        adapter.submitList(data);
    }

    public void onInitFail(String msg) {
        PromptUtil.snackbarShowTxt(root, "数据初始化失败: " + msg);
        progressBar.setVisibility(View.GONE);
    }

    public void onAuthorizeSuccess() {
        PromptUtil.snackbarShowTxt(root, "授权成功！");
        //todo:授权成功后如何直接显示而不是刷新所有,recyclerview如何在某一项变化时局部刷新
        presenter.initData(repository, args.getInt("associationId"));
    }

    public void onAuthorizeFail() {
        PromptUtil.snackbarShowTxt(root, "授权失败！");
    }

    /**
     * todo:内部类为何就能共享数据？？
     * 管理员长按成员列表项，弹出授权框
     */
    class AuthorizeMemberListener extends RecyclerView.SimpleOnItemTouchListener {
        //对手势交互的监听接口有默认实现的类，避免implement不需要的方法
        private GestureDetector.SimpleOnGestureListener gl = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                //获取当前触摸点下，recyclerview的子项
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                int position = recyclerView.getChildAdapterPosition(child);
                User user = adapter.getItem(position);
                //已经是管理员
                if (user.getState() == 2)
                    return;
                showAuthorizeDialog(user.getId());
            }
        };

        //识别手势类的Compat（兼容）版本
        private GestureDetectorCompat mGestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(), gl);

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            return mGestureDetectorCompat.onTouchEvent(e);
        }

        /**
         * 弹出授权框
         *
         * @param memberId 此处为授权目标的id
         */
        private void showAuthorizeDialog(int memberId) {
            new AlertDialog.Builder(getActivity())
                    .setMessage("确认授权该用户为管理员？")
                    .setCancelable(false)
                    .setPositiveButton(R.string.txt_confirm, (dialog, id) -> {
                        authorizeMember(memberId);
                    })
                    .setNegativeButton(R.string.txt_cancel, (dialog, id) -> {
                        dialog.cancel();
                    })
                    .create().show();
        }

    }

}
