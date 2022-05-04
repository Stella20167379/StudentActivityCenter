package com.example.graduatedesign.student_activity_module.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.student_activity_module.adapter.viewholder.ActivityCommentViewHolder;
import com.example.graduatedesign.student_activity_module.data.Comment;

import java.util.List;

public class ActivityCommentAdapter extends RecyclerView.Adapter<ActivityCommentViewHolder> {
    private final AsyncListDiffer<Comment> mDiffer;
    private final DiffUtil.ItemCallback<Comment> diffCallback = new DiffUtil.ItemCallback<Comment>() {

        @Override
        public boolean areItemsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
            return oldItem.getActivityId() == newItem.getActivityId() && oldItem.getUserId() == newItem.getUserId();
        }
    };

    public ActivityCommentAdapter() {
        mDiffer = new AsyncListDiffer<>(this, diffCallback);
    }

    @NonNull
    @Override
    public ActivityCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ActivityCommentViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityCommentViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<Comment> data) {
        mDiffer.submitList(data);
    }

    public Comment getItem(int position) {
        return mDiffer.getCurrentList().get(position);
    }

}
