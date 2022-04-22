package com.example.graduatedesign.adapter.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.R;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.utils.GlideUtils;

public class HomeFragmentHolder extends RecyclerView.ViewHolder {

    private final View itemView;

    //活动封面
    private final ImageView cover;
    //活动名称
    private final TextView title;
    //举办活动的社团名称
    private final TextView association;
    //活动报名时间
    private final TextView signTime;
    //活动人数上限
    private final TextView sumLimit;
    //活动状态
    private final TextView state;

    public HomeFragmentHolder(@NonNull View itemView) {
        super(itemView);

        this.itemView = itemView;

        cover = itemView.findViewById(R.id.cover);
        title = itemView.findViewById(R.id.activity_title);
        association = itemView.findViewById(R.id.association);
        signTime = itemView.findViewById(R.id.signTime_title);
        sumLimit = itemView.findViewById(R.id.sumLimit);
        state = itemView.findViewById(R.id.state);
    }

    public static HomeFragmentHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_show, parent, false);
        return new HomeFragmentHolder(view);
    }

    public void bind(MyStudentActivity studentActivity) {
        if (studentActivity == null)
            return;

        int resourceId = R.drawable.horizontal_doggy;

        Glide.with(itemView)
                .load(resourceId)
                .apply(GlideUtils.OPTIONS)
                .into(cover);
        title.setText(studentActivity.getTitle());
        association.setText(studentActivity.getAssociationName());
        signTime.setText("时间：2022/22/2");
        sumLimit.setText("studentActivity.getSumLimit()");
        state.setText(studentActivity.getState());
    }

}
