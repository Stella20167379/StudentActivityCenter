package com.example.graduatedesign.personal_module.ui.association;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.association_module.data.Association;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.databinding.FragmentWithOneRecyclerviewBinding;
import com.example.graduatedesign.personal_module.adapter.MyAssociationAdapter;
import com.example.graduatedesign.personal_module.data.User;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyAssociationFragment extends Fragment {
    private FragmentWithOneRecyclerviewBinding binding;
    private View root;
    private RecyclerView recyclerView;
    private MyAssociationAdapter adapter;
    private MyAssociationPresenter presenter;

    private ProgressBar progressBar;

    @Inject
    MyRepository repository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentWithOneRecyclerviewBinding.inflate(inflater,container,false);
        root=binding.getRoot();
        presenter=new MyAssociationPresenter(this);
        progressBar= binding.progressBar;
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView title= binding.toolbarTitle;
        final Toolbar toolbar = binding.toolbar;
        recyclerView= binding.recyclerView;
        adapter=new MyAssociationAdapter();

        //绑定返回功能
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(root)
                .popBackStack());
        title.setText("我的社团");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        final MainActivityViewModel viewModel=new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        User user=viewModel.getUserInfo().getValue();
        presenter.initData(repository, user.getId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root=null;
        binding=null;
        presenter= null;
    }

    public void onInitSuccess(List<Association> data){
        adapter.submitList(data);
    }

    /**
     * 查询失败，或没有数据
     */
    public void onInitFail(){
        progressBar.setVisibility(View.INVISIBLE);
    }
}
