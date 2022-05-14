package com.example.graduatedesign.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class MyPagerAdapter extends FragmentStateAdapter {
    private final List<String> fragmentClazzList;
    private Bundle bundle = null;

    public MyPagerAdapter(@NonNull Fragment fragment, List<String> fragmentClazzList) {
        super(fragment);
        this.fragmentClazzList = fragmentClazzList;
    }

    public MyPagerAdapter(@NonNull Fragment fragment, List<String> fragmentClazzList, Bundle bundle) {
        super(fragment);
        this.fragmentClazzList = fragmentClazzList;
        this.bundle = bundle;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        try {
            Fragment fragment = (Fragment) Class.forName(fragmentClazzList.get(position)).newInstance();
            if (this.bundle != null)
                fragment.setArguments(bundle);
            return fragment;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return fragmentClazzList.size();
    }

}
