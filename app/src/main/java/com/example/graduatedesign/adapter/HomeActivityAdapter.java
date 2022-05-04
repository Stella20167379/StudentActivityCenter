package com.example.graduatedesign.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.example.graduatedesign.adapter.viewholder.HomeActivityHolder;
import com.example.graduatedesign.data.model.MyStudentActivity;

public class HomeActivityAdapter extends PagingDataAdapter<MyStudentActivity, HomeActivityHolder> {

    public HomeActivityAdapter(@NonNull DiffUtil.ItemCallback<MyStudentActivity> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public HomeActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return HomeActivityHolder.create(parent);
    }


    @Override
    public void onBindViewHolder(@NonNull HomeActivityHolder holder, int position) {
        MyStudentActivity studentActivity = getItem(position);
        holder.bind(studentActivity);
    }

    public static class HomeActivityComparator extends DiffUtil.ItemCallback<MyStudentActivity> {

        @Override
        public boolean areItemsTheSame(@NonNull MyStudentActivity oldItem, @NonNull MyStudentActivity newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MyStudentActivity oldItem, @NonNull MyStudentActivity newItem) {
            return oldItem.getId() == newItem.getId();
        }
    }

    public MyStudentActivity getAdapterItem(int position){
        return getItem(position);
    }

}


