package com.example.graduatedesign.ui.bulletin.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.ui.bulletin.adapter.viewholder.SimpleBulletinHolder;
import com.example.graduatedesign.ui.bulletin.data.SimpleBulletin;

import java.util.List;

public class SimpleBulletinAdapter extends RecyclerView.Adapter<SimpleBulletinHolder> {
    private final AsyncListDiffer<SimpleBulletin> mDiffer;

    private final DiffUtil.ItemCallback<SimpleBulletin> diffCallback=new DiffUtil.ItemCallback<SimpleBulletin>() {
        @Override
        public boolean areItemsTheSame(@NonNull SimpleBulletin oldItem, @NonNull SimpleBulletin newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull SimpleBulletin oldItem, @NonNull SimpleBulletin newItem) {
            return oldItem.getId() == newItem.getId();
        }
    };

    public SimpleBulletinAdapter() {
        mDiffer = new AsyncListDiffer<>(this, diffCallback);
    }
    
    @NonNull
    @Override
    public SimpleBulletinHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SimpleBulletinHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleBulletinHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<SimpleBulletin> data) {
        mDiffer.submitList(data);
    }

    public SimpleBulletin getItem(int position) {
        return mDiffer.getCurrentList().get(position);
    }
}
