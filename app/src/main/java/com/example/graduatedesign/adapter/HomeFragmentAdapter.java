package com.example.graduatedesign.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.example.graduatedesign.adapter.viewholder.HomeFragmentHolder;
import com.example.graduatedesign.data.model.MyStudentActivity;

public class HomeFragmentAdapter extends PagingDataAdapter<MyStudentActivity, HomeFragmentHolder> {

    public HomeFragmentAdapter(@NonNull DiffUtil.ItemCallback<MyStudentActivity> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public HomeFragmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return HomeFragmentHolder.create(parent);
    }


    @Override
    public void onBindViewHolder(@NonNull HomeFragmentHolder holder, int position) {
        MyStudentActivity studentActivity = getItem(position);
        holder.bind(studentActivity);
    }

    public static class HomeFragmentComparator extends DiffUtil.ItemCallback<MyStudentActivity> {

        @Override
        public boolean areItemsTheSame(@NonNull MyStudentActivity oldItem, @NonNull MyStudentActivity newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MyStudentActivity oldItem, @NonNull MyStudentActivity newItem) {
            return oldItem.getId() == newItem.getId();
        }
    }

}


