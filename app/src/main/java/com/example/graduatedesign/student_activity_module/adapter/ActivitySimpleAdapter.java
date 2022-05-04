package com.example.graduatedesign.student_activity_module.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.student_activity_module.adapter.viewholder.ActivitySimpleHolder;

import java.util.List;

public class ActivitySimpleAdapter extends RecyclerView.Adapter<ActivitySimpleHolder> {
    private final AsyncListDiffer<MyStudentActivity> mDiffer;
    private final DiffUtil.ItemCallback<MyStudentActivity> diffCallback=new DiffUtil.ItemCallback<MyStudentActivity>() {

        @Override
        public boolean areItemsTheSame(@NonNull MyStudentActivity oldItem, @NonNull MyStudentActivity newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MyStudentActivity oldItem, @NonNull MyStudentActivity newItem) {
            return oldItem.getId() == newItem.getId();
        }
    };

    public ActivitySimpleAdapter() {
        mDiffer = new AsyncListDiffer<>(this, diffCallback);
    }


    @NonNull
    @Override
    public ActivitySimpleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ActivitySimpleHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivitySimpleHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<MyStudentActivity> data) {
        mDiffer.submitList(data);
    }

    public MyStudentActivity getItem(int position) {
        return mDiffer.getCurrentList().get(position);
    }
}
