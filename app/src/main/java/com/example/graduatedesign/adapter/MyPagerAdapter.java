package com.example.graduatedesign.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class MyPagerAdapter extends FragmentStateAdapter {
    private final List<String> fragmentClazzList;

    public MyPagerAdapter(@NonNull Fragment fragment, List<String> fragmentClazzList) {
        super(fragment);
        this.fragmentClazzList=fragmentClazzList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        try {
            return (Fragment) Class.forName(fragmentClazzList.get(position)).newInstance();
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
