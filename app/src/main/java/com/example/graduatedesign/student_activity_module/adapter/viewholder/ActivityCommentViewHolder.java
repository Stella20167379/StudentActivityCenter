package com.example.graduatedesign.student_activity_module.adapter.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.R;
import com.example.graduatedesign.student_activity_module.data.Comment;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GlideUtils;
import com.google.android.material.imageview.ShapeableImageView;

public class ActivityCommentViewHolder extends RecyclerView.ViewHolder {
    private final View itemView;

    private final ShapeableImageView imageView;
    private final TextView commentView;
    private final TextView commentScore;


    private ActivityCommentViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView=itemView;

        imageView=itemView.findViewById(R.id.senderImg);
        commentView =itemView.findViewById(R.id.comment);
        commentScore =itemView.findViewById(R.id.comment_score);
    }

    public static ActivityCommentViewHolder create(ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new ActivityCommentViewHolder(view);
    }

    public void bind(Comment comment) {
        if (comment == null)
            return;
        Glide.with(itemView)
                .load(DataUtil.getImgDownloadUri(comment.getSenderImg()))
                .apply(GlideUtils.OPTIONS)
                .into(imageView);
        commentView.setText(comment.getContent());
        commentScore.setText(String.valueOf(comment.getScore()));

    }
}
