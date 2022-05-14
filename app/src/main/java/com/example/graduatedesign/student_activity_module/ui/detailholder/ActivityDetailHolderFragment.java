package com.example.graduatedesign.student_activity_module.ui.detailholder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.graduatedesign.R;
import com.example.graduatedesign.adapter.MyPagerAdapter;
import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.databinding.FragmentActivityDetailHolderBinding;
import com.example.graduatedesign.student_activity_module.ui.comment.ActivityCommentFragment;
import com.example.graduatedesign.student_activity_module.ui.detail.ActivityDetailFragment;
import com.example.graduatedesign.utils.PromptUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ActivityDetailHolderFragment extends Fragment {
    @Inject
    MyRepository repository;
    Integer activityId = null;
    private FragmentActivityDetailHolderBinding binding;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActivityDetailHolderBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        Bundle args = getArguments();
        if (args == null) {
            PromptUtil.snackbarShowTxt(root, "不知道该展示哪个活动的详情，没有传参！");
            navController.popBackStack();
            return;
        }
        activityId = args.getInt("id");

        final ViewPager2 viewPager = view.findViewById(R.id.viewpager);
        final TabLayout tabLayout = view.findViewById(R.id.tabs);

        /* 要创建的页面的全限定名 */
        List<String> clazzList = new ArrayList<>();
        clazzList.add(ActivityDetailFragment.class.getName());
        clazzList.add(ActivityCommentFragment.class.getName());
        /* 初始化tab栏和viewpager */
        final MyPagerAdapter adapter = new MyPagerAdapter(this, clazzList, args);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0)
                        tab.setText(R.string.title_activity_detail);
                    else if (position == 1)
                        tab.setText(R.string.title_activity_comment);
                }
        ).attach();

        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
        binding = null;
    }

}