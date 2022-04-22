package com.example.graduatedesign.student_activity_module.ui.detail;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ActivityPagerAdapter extends FragmentStateAdapter {

    public ActivityPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position==0){
            fragment=new ActivityDetailFragment();
        }else
            fragment=new ActivityCommentFragment();
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
