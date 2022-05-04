package com.example.graduatedesign.personal_module.adapter.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.R;
import com.example.graduatedesign.personal_module.data.PayRecord;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GlideUtils;

public class MyPayRecordHolder extends RecyclerView.ViewHolder {

    private final View itemView;

    private ImageView cover;
    private TextView activityName;
    private TextView amount;

    private MyPayRecordHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        cover = itemView.findViewById(R.id.cover);
        activityName = itemView.findViewById(R.id.activity_name);
        amount = itemView.findViewById(R.id.amount);
    }


    public static MyPayRecordHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pay_record, parent, false);
        return new MyPayRecordHolder(view);
    }

    public void bind(PayRecord record){
        if (record == null)
            return;

        Glide.with(itemView)
                .load(DataUtil.getImgDownloadUri(record.getActivityCover()))
                .apply(GlideUtils.OPTIONS)
                .into(cover);
        activityName.setText(record.getActivityName());
        amount.setText(String.valueOf(record.getAmount()));
    }

}
