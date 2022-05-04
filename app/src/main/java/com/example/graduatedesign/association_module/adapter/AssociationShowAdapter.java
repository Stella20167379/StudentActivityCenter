package com.example.graduatedesign.association_module.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.association_module.data.Association;
import com.example.graduatedesign.association_module.adapter.viewholder.AssociationShowHolder;

import java.util.List;

public class AssociationShowAdapter extends RecyclerView.Adapter<AssociationShowHolder> {
    private final AsyncListDiffer<Association> mDiffer;
    private final DiffUtil.ItemCallback<Association> diffCallback=new DiffUtil.ItemCallback<Association>() {
        @Override
        public boolean areItemsTheSame(@NonNull Association oldItem, @NonNull Association newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Association oldItem, @NonNull Association newItem) {
            return oldItem.getId() == newItem.getId();
        }
    };

    public AssociationShowAdapter() {
        mDiffer = new AsyncListDiffer<>(this, diffCallback);
    }

    @NonNull
    @Override
    public AssociationShowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return AssociationShowHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull AssociationShowHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<Association> data) {
        mDiffer.submitList(data);
    }

    public Association getItem(int position) {
        return mDiffer.getCurrentList().get(position);
    }
}
