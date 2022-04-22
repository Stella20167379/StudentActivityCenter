package com.example.graduatedesign.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.adapter.viewholder.MessageHolder;
import com.example.graduatedesign.data.model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {
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

    public MessageAdapter() {
        mDiffer = new AsyncListDiffer<>(this, diffCallback);
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MessageHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        /* 对MessageHolder进行特定处理，如添加监听器等 */
        Message message =getItem(position);
        holder.bind(message);
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
