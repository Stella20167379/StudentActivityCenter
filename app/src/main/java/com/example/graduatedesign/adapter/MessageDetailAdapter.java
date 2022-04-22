package com.example.graduatedesign.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.adapter.viewholder.MessageDetailHolder;
import com.example.graduatedesign.data.model.Message;

import java.util.List;

public class MessageDetailAdapter extends RecyclerView.Adapter<MessageDetailHolder> {
    private final AsyncListDiffer<Message> mDiffer;
    private final DiffUtil.ItemCallback<Message> diffCallback=new DiffUtil.ItemCallback<Message>() {
        @Override
        public boolean areItemsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.getId() == newItem.getId();
        }
    };

    public MessageDetailAdapter() {
        mDiffer = new AsyncListDiffer<>(this, diffCallback);
    }

    @NonNull
    @Override
    public MessageDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MessageDetailHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageDetailHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<Message> data) {
        mDiffer.submitList(data);
    }

    public Message getItem(int position) {
        return mDiffer.getCurrentList().get(position);
    }

}
