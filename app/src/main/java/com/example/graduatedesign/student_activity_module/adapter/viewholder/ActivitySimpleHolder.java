package com.example.graduatedesign.student_activity_module.adapter.viewholder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.R;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GlideUtils;

import java.time.LocalDateTime;

public class ActivitySimpleHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "ActivitySimpleHolder";

    private final View itemView;
    private final ImageView cover;
    private final TextView title;
    private final TextView activityTime;
    private final TextView stateView;

    private ActivitySimpleHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        cover=itemView.findViewById(R.id.cover);
        title=itemView.findViewById(R.id.association_name);
        activityTime=itemView.findViewById(R.id.duration_time);
        stateView =itemView.findViewById(R.id.activity_state);
    }

    public static ActivitySimpleHolder create(ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_simple, parent, false);
        return new ActivitySimpleHolder(view);
    }

    public void bind(MyStudentActivity activity) {
        if (activity == null)
            return;
        Glide.with(itemView)
                .load(GlideUtils.getImgDownloadUri(activity.getCoverImg()))
                .apply(GlideUtils.OPTIONS)
                .into(cover);
        title.setText(activity.getTitle());

        String timeStr = activity.getActivityStart().substring(0, 10) + "-" + activity.getActivityEnd().substring(0, 10);
        activityTime.setText(timeStr);

        String state = "已结束";
        if (activity.getParticipantState() != null) {
            Log.d(TAG, "查找参与状态中 ");
            switch (activity.getParticipantState()) {
                case 1:
                    state = "未签到";
                    break;
                case 2:
                    state = "已签到";
                    break;
                case 3:
                    state = "已签退";
                    break;
            }
        } else {
            String nowStr = DataUtil.dateToString(LocalDateTime.now(), true);
            Log.d(TAG, "时间比对: " + nowStr);
            if (DataUtil.compareDatetimeStr(activity.getActivityStart(), nowStr))
                state = "未开始";
            else if (DataUtil.compareDatetimeStr(activity.getActivityEnd(), nowStr))
                state = "进行中";
        }
        stateView.setText(state);
    }
}
