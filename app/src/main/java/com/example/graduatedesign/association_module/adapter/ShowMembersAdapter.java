package com.example.graduatedesign.association_module.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.association_module.adapter.viewholder.ShowMembersHolder;
import com.example.graduatedesign.personal_module.data.User;

import java.util.List;

public class ShowMembersAdapter extends RecyclerView.Adapter<ShowMembersHolder> {
    private final AsyncListDiffer<User> mDiffer;
    private final DiffUtil.ItemCallback<User> diffCallback=new DiffUtil.ItemCallback<User>() {
        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getId() == newItem.getId();
        }
    };

    public ShowMembersAdapter(){
        mDiffer = new AsyncListDiffer<>(this, diffCallback);
    }

    @NonNull
    @Override
    public ShowMembersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ShowMembersHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowMembersHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<User> data) {
        mDiffer.submitList(data);
    }

    public User getItem(int position) {
        return mDiffer.getCurrentList().get(position);
    }
}
