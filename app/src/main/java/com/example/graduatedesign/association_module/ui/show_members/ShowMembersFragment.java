package com.example.graduatedesign.association_module.ui.show_members;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.R;
import com.example.graduatedesign.association_module.adapter.ShowMembersAdapter;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.databinding.FragmentWithOneRecyclerviewBinding;
import com.example.graduatedesign.personal_module.data.User;

import java.util.List;

import javax.inject.Inject;

public class ShowMembersFragment extends Fragment {
    private FragmentWithOneRecyclerviewBinding binding;
    private View root;
    private RecyclerView recyclerView;
    private ShowMembersPresenter presenter;
    private ShowMembersAdapter adapter;

    @Inject
    MyRepository repository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentWithOneRecyclerviewBinding.inflate(inflater,container,false);
        root=binding.getRoot();
        presenter=new ShowMembersPresenter(this);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView title= binding.toolbarTitle;
        final Toolbar toolbar = binding.toolbar;
        recyclerView= binding.recyclerView;
        adapter=new ShowMembersAdapter();
        //绑定返回功能
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(root)
                .popBackStack());
        title.setText("社团成员");

        Bundle args=getArguments();
        int associationId=args.getInt("associationId");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(new AuthorizeMemberListener());

        presenter.initData(repository,associationId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root=null;
        binding=null;
        presenter= null;
    }

    public void onInitSuccess(List<User> data){
        adapter.submitList(data);
    }

    /**
     * 管理员长按成员列表项，弹出授权框
     */
    class AuthorizeMemberListener extends RecyclerView.SimpleOnItemTouchListener {
        //对手势交互的监听接口有默认实现的类，避免implement不需要的方法
        private GestureDetector.SimpleOnGestureListener gl = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                //获取当前触摸点下，recyclerview的子项
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                int position= (int) recyclerView.getChildItemId(child);
                User user=adapter.getItem(position);
                //todo:待完成
            }
        };

        //识别手势类的Compat（兼容）版本
        private GestureDetectorCompat mGestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(), gl);

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            return mGestureDetectorCompat.onTouchEvent(e);
        }
    }

}
