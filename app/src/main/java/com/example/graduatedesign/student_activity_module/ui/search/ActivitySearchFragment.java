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
import com.example.graduatedesign.utils.PromptUtil;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;


public class ActivitySearchFragment extends Fragment {
    private static final String TAG = "ActivitySearchFragment";
    private FragmentActivitySearchBinding binding;
    private View root;
    private RecyclerView recyclerView;
    private ActivitySimpleAdapter adapter;

    @Inject
    MyRepository repository;
    private ActivitySearchPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentActivitySearchBinding.inflate(inflater,container,false);
        root=binding.getRoot();
        recyclerView= binding.recyclerView;
        presenter=new ActivitySearchPresenter(this);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Toolbar toolbar=binding.BackToolbar;
        toolbar.setNavigationOnClickListener(v->{
            final NavController navController= Navigation.findNavController(root);
            navController.popBackStack();
        });

        final CalendarView calendarView= binding.calendarView;
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Log.d(TAG, "选中日期: "+year+"\t"+month+"\t"+dayOfMonth);
            String dateStr = String.format(Locale.CHINA, "%d-%d-%d", year, month,dayOfMonth);
            presenter.searchActivities(repository,dateStr);
        });
        adapter=new ActivitySimpleAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root=null;
        binding=null;
    }

    public void onSearchSuccess(List<MyStudentActivity> activities){
        adapter.submitList(activities);
    }

    public void onSearchFail(){
        PromptUtil.snackbarShowTxt(root,"没有活动~ ");
        //todo: 显示没有数据的图片
    }
}