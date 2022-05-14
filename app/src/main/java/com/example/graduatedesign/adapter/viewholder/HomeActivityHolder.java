package com.example.graduatedesign.adapter.viewholder;

import android.util.Log;
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
    private static final String TAG = "HomeActivityHolder";

    private final View itemView;

    //活动封面
    private final ImageView cover;
    //活动名称
    private final TextView title;
    //举办活动的社团名称
    private final TextView association;
    //活动报名时间
    private final TextView signTime;
    //活动状态
    private final TextView stateView;
    //活动收费
    private final TextView chargeAmount;

    public HomeActivityHolder(@NonNull View itemView) {
        super(itemView);

        this.itemView = itemView;

        cover = itemView.findViewById(R.id.cover);
        title = itemView.findViewById(R.id.activity_title);
        association = itemView.findViewById(R.id.association_title);
        signTime = itemView.findViewById(R.id.signTime_title);
        stateView = itemView.findViewById(R.id.state);
        chargeAmount = itemView.findViewById(R.id.charge_amount);
    }

    public static HomeActivityHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_show, parent, false);
        return new HomeActivityHolder(view);
    }

    public void bind(MyStudentActivity studentActivity) {
        if (studentActivity == null)
            return;

        Log.d(TAG, "bind获取收费金额为： " + studentActivity.getChargeAmount());
        if (studentActivity.getChargeAmount() != null && studentActivity.getChargeAmount() != 0)
            chargeAmount.setText(studentActivity.getChargeAmount() + "元");

        Glide.with(itemView)
                .load(GlideUtils.getImgDownloadUri(studentActivity.getCoverImg()))
                .apply(GlideUtils.OPTIONS)
                .into(cover);
        title.setText(studentActivity.getTitle());
        association.setText(studentActivity.getAssociationName());
        String activityTime = studentActivity.getSignStart().substring(0, 10) + "-" + studentActivity.getSignEnd().substring(0, 10);
        signTime.setText(activityTime);

        String state = "已结束";
        String nowStr = DataUtil.dateToString(LocalDateTime.now(), true);
        if (DataUtil.compareDatetimeStr(studentActivity.getActivityStart(), nowStr))
            state = "未开始";
        else if (DataUtil.compareDatetimeStr(studentActivity.getActivityEnd(), nowStr))
            state = "进行中";
        stateView.setText(state);
    }

}
