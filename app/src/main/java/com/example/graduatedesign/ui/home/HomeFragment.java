package com.example.graduatedesign.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.adapter.HomeFragmentAdapter;
import com.example.graduatedesign.databinding.FragmentHomeBinding;
import com.example.graduatedesign.utils.RxLifecycleUtils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private View root;
    private HomeViewModel viewModel;

    private RecyclerView recyclerView;
    private HomeFragmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        recyclerView = binding.activitiesRecyclerView;
        adapter = new HomeFragmentAdapter(new HomeFragmentAdapter.HomeFragmentComparator());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        viewModel=new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.getActivities(20)
                .to(RxLifecycleUtils.bindLifecycle(this))
                .subscribe(pagingData -> adapter.submitData(getLifecycle(),pagingData));

        final ProgressBar progressBar= binding.progressBar;
        adapter.addLoadStateListener(
                combinedLoadStates -> {
                    LoadState state=combinedLoadStates.getRefresh();
                    if (state instanceof LoadState.Loading){
                        Log.d(TAG, "Loading: 加载中");
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    else if (state instanceof LoadState.Error){
                        Log.d(TAG, "Error: 出错了");
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    else if (state instanceof LoadState.NotLoading){
                        Log.d(TAG, "NotLoading: 加载完毕");
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    return null;
                }
        );

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root=null;
        binding=null;
    }

}

