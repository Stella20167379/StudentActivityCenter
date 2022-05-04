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
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GlideUtils;

import java.time.LocalDateTime;

public class HomeActivityHolder extends RecyclerView.ViewHolder {

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
    private final TextView stateView;

    public HomeActivityHolder(@NonNull View itemView) {
        super(itemView);

        this.itemView = itemView;

        cover = itemView.findViewById(R.id.cover);
        title = itemView.findViewById(R.id.activity_title);
        association = itemView.findViewById(R.id.association_title);
        signTime = itemView.findViewById(R.id.signTime_title);
        sumLimit = itemView.findViewById(R.id.sumLimit);
        stateView = itemView.findViewById(R.id.state);
    }

    public static HomeActivityHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_show, parent, false);
        return new HomeActivityHolder(view);
    }

    public void bind(MyStudentActivity studentActivity) {
        if (studentActivity == null)
            return;

        Glide.with(itemView)
                .load(DataUtil.getImgDownloadUri(studentActivity.getCoverImg()))
                .apply(GlideUtils.OPTIONS)
                .into(cover);
        title.setText(studentActivity.getTitle());
        association.setText(studentActivity.getAssociationName());
        signTime.setText(studentActivity.getSignStart()+"-"+studentActivity.getSignEnd());
        sumLimit.setText(studentActivity.getSumLimit());

        String state="已结束";
        String nowStr = DataUtil.dateToString(LocalDateTime.now(), true);
        if (DataUtil.compareDatetimeStr(studentActivity.getActivityStart(), nowStr))
            state = "未开始";
        else if (DataUtil.compareDatetimeStr(studentActivity.getActivityEnd(), nowStr))
            state = "进行中";
        stateView.setText(state);
    }

}
