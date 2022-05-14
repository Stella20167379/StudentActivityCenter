package com.example.graduatedesign.student_activity_module.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.databinding.FragmentActivitySearchBinding;
import com.example.graduatedesign.student_activity_module.adapter.ActivitySimpleAdapter;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.PromptUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ActivitySearchFragment extends Fragment {
    private static final String TAG = "ActivitySearchFragment";
    private FragmentActivitySearchBinding binding;
    private View root;
    private RecyclerView recyclerView;
    private ActivitySimpleAdapter adapter;
    /* 学校id */
    private Bundle args;

    @Inject
    MyRepository repository;
    private ActivitySearchPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActivitySearchBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        recyclerView = binding.recyclerView;
        presenter = new ActivitySearchPresenter(this);
        /* 别忘了加上观察者，才能起作用啊！ */
        getLifecycle().addObserver(presenter);

        args = getArguments();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Toolbar toolbar = binding.BackToolbar;
        final NavController navController = Navigation.findNavController(root);
        if (args == null) {
            PromptUtil.snackbarShowTxt(root, "没有收到参数！");
            navController.popBackStack();
            return;
        }
        toolbar.setNavigationOnClickListener(v -> {
            navController.popBackStack();
        });
        int schoolId = args.getInt("schoolId");

        final CalendarView calendarView = binding.calendarView;
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Log.d(TAG, "选中日期: " + year + "\t" + month + "\t" + dayOfMonth);
            //选中的月份是从0开始的
            String dateStr = String.format(Locale.CHINA, "%d-%d-%d", year, month + 1, dayOfMonth);
            presenter.searchActivities(repository, dateStr, schoolId);
        });
        adapter = new ActivitySimpleAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        // 初始化数据为搜索当天的结果
        String todayStr = DataUtil.dateToString(LocalDateTime.now(), false);
        presenter.searchActivities(repository, todayStr, schoolId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

    public void onSearchSuccess(List<MyStudentActivity> activities) {
        adapter.submitList(activities);
    }

    public void onSearchFail() {
        PromptUtil.snackbarShowTxt(root, "没有活动~ ");
        adapter.submitList(null);
    }
}