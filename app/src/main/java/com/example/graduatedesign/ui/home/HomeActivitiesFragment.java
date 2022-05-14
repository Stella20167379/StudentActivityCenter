package com.example.graduatedesign.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.MainActivityViewModel;
import com.example.graduatedesign.R;
import com.example.graduatedesign.adapter.HomeActivityAdapter;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.databinding.FragmentWithOneRecyclerviewBinding;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.utils.RxLifecycleUtils;

public class HomeActivitiesFragment extends Fragment {
    private static final String TAG = "HomeActivitiesFragment";
    private FragmentWithOneRecyclerviewBinding binding;
    private View root;
    private HomeViewModel viewModel;
    private RecyclerView recyclerView;
    private HomeActivityAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWithOneRecyclerviewBinding.inflate(inflater, container, false);
        root = binding.getRoot();
//        viewModel=new ViewModelProvider(getParentFragment()).get(HomeViewModel.class);
        viewModel=new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        adapter = new HomeActivityAdapter(new HomeActivityAdapter.HomeActivityComparator());

        //不需要显示顶部工具栏
        final Toolbar toolbar=binding.toolbar;
        toolbar.setVisibility(View.GONE);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final User user = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class).getUserInfo().getValue();
        final ProgressBar progressBar = binding.progressBar;

        recyclerView = binding.recyclerView;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new ActivityItemListener());

        adapter.addLoadStateListener(
                combinedLoadStates -> {
                    LoadState state = combinedLoadStates.getRefresh();
                    if (state instanceof LoadState.Loading){
                        Log.d(TAG, "Loading: 加载中");
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    else if (state instanceof LoadState.Error){
                        Log.d(TAG, "Error: 出错了");
                        progressBar.setVisibility(View.INVISIBLE);
                    } else if (state instanceof LoadState.NotLoading) {
                        Log.d(TAG, "NotLoading: 加载完毕");
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    return null;
                }
        );

        viewModel.getSearchKey().observe(getViewLifecycleOwner(), key -> {
            if (viewModel.getTabOpt() != 0)
                return;
            Log.d(TAG, "onViewCreated: 进入活动搜索");
            keySearchActivity(key, user.getSchoolId());
        });

        keySearchActivity(null, user.getSchoolId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root=null;
        binding=null;
    }

    class ActivityItemListener extends RecyclerView.SimpleOnItemTouchListener {
        //对手势交互的监听接口有默认实现的类，避免implement不需要的方法
        private GestureDetector.SimpleOnGestureListener gl = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //获取当前触摸点下，recyclerview的子项
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                int position = (int) recyclerView.getChildAdapterPosition(child);

                /* 放误点 */
                if (position < 0)
                    return false;

                Log.d(TAG, "onSingleTapUp，点击获取的索引:" + position);
                MyStudentActivity activity = adapter.getAdapterItem(position);
                //携带数据跳转页面
                Bundle bundle = new Bundle();
                bundle.putInt("id", activity.getId());
                final NavController navController = Navigation.findNavController(recyclerView);
                navController.navigate(R.id.navigation_activity_detail_holder, bundle);
                return false;
            }
        };

        //识别手势类的Compat（兼容）版本
        private GestureDetectorCompat mGestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(), gl);

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            return mGestureDetectorCompat.onTouchEvent(e);
        }
    }

    /**
     * 使用关键字重建活动数据源，recyclerview视图自己调用数据源绑定的方法进行查询
     * 注：传入空值时，等同刷新功能
     * @param key 搜索的关键字
     */
    private void keySearchActivity(String key,int schoolId){
        viewModel.getActivities(20,schoolId,key)
                .to(RxLifecycleUtils.bindLifecycle(this))
                .subscribe(pagingData -> adapter.submitData(getLifecycle(),pagingData));
    }

}
