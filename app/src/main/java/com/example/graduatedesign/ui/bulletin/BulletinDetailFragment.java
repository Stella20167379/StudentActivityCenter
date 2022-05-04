package com.example.graduatedesign.ui.bulletin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.databinding.FragmentWithOneRecyclerviewBinding;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.personal_module.ui.comment.MyCommentPresenter;
import com.example.graduatedesign.student_activity_module.data.Comment;
import com.example.graduatedesign.ui.bulletin.adapter.SimpleBulletinAdapter;
import com.example.graduatedesign.ui.bulletin.data.SimpleBulletin;
import com.example.graduatedesign.utils.PromptUtil;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BulletinDetailFragment extends Fragment {
    private FragmentWithOneRecyclerviewBinding binding;
    private View root;
    private RecyclerView recyclerView;
    private SimpleBulletinAdapter adapter;
    private ProgressBar progressBar;

    @Inject
    MyRepository repository;
    private BulletinDetailPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentWithOneRecyclerviewBinding.inflate(inflater,container,false);
        root=binding.getRoot();
        progressBar= binding.progressBar;
        presenter=new BulletinDetailPresenter(this);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        Bundle args = getArguments();
        if (args == null) {
            PromptUtil.snackbarShowTxt(root, "不知道该展示什么公告，没有传参！");
            navController.popBackStack();
            return;
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter=new SimpleBulletinAdapter();
        recyclerView.setAdapter(adapter);
        //type:1-活动公告,2-社团公告
        presenter.initData(args.getInt("type"),repository,args.getInt("ownerId"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root=null;
        binding=null;
        presenter= null;
    }

    public void onInitSuccess(List<SimpleBulletin> data){
        adapter.submitList(data);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * 查询失败，或没有数据
     */
    public void onInitFail(){
        progressBar.setVisibility(View.INVISIBLE);
    }
}