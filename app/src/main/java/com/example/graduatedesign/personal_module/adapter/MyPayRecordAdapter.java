package com.example.graduatedesign.personal_module.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.personal_module.adapter.viewholder.MyPayRecordHolder;
import com.example.graduatedesign.personal_module.data.PayRecord;

import java.util.List;

public class MyPayRecordAdapter extends RecyclerView.Adapter<MyPayRecordHolder> {
    private final AsyncListDiffer<PayRecord> mDiffer;
    private final DiffUtil.ItemCallback<PayRecord> diffCallback=new DiffUtil.ItemCallback<PayRecord>() {
        @Override
        public boolean areItemsTheSame(@NonNull PayRecord oldItem, @NonNull PayRecord newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull PayRecord oldItem, @NonNull PayRecord newItem) {
            return oldItem.getId() == newItem.getId();
        }
    };

    public MyPayRecordAdapter() {
        mDiffer = new AsyncListDiffer<>(this, diffCallback);
    }

    @NonNull
    @Override
    public MyPayRecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MyPayRecordHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPayRecordHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<PayRecord> data) {
        mDiffer.submitList(data);
    }

    public PayRecord getItem(int position) {
        return mDiffer.getCurrentList().get(position);
    }
}
