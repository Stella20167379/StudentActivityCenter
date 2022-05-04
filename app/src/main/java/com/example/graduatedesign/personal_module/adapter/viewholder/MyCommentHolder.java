package com.example.graduatedesign.personal_module.adapter.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.R;
import com.example.graduatedesign.adapter.viewholder.MessageHolder;
import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.student_activity_module.data.Comment;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GlideUtils;

public class MyCommentHolder extends RecyclerView.ViewHolder {
    private final View itemView;

    private ImageView senderImg;
    private TextView commentView;
    private TextView score;

    private MyCommentHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        senderImg = itemView.findViewById(R.id.senderImg);
        commentView = itemView.findViewById(R.id.comment);
        score = itemView.findViewById(R.id.comment_score);
    }


    public static MyCommentHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new MyCommentHolder(view);
    }

    public void bind(Comment comment){
        if (comment == null)
            return;

        Glide.with(itemView)
                .load(DataUtil.getImgDownloadUri(comment.getSenderImg()))
                .apply(GlideUtils.OPTIONS)
                .into(senderImg);
        commentView.setText(comment.getContent());
        score.setText(String.valueOf(comment.getScore()));
    }

}
