package com.example.graduatedesign.personal_module.adapter.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.R;
import com.example.graduatedesign.association_module.data.Association;
import com.example.graduatedesign.student_activity_module.data.Comment;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GlideUtils;

public class MyAssociationHolder extends RecyclerView.ViewHolder {

    private final View itemView;

    private ImageView cover;
    private TextView associationName;
    private TextView establishTime;

    private MyAssociationHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        cover = itemView.findViewById(R.id.cover);
        associationName = itemView.findViewById(R.id.association_name);
        establishTime = itemView.findViewById(R.id.establish_time);
    }


    public static MyAssociationHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_association_simple, parent, false);
        return new MyAssociationHolder(view);
    }

    public void bind(Association association){
        if (association == null)
            return;

        Glide.with(itemView)
                .load(DataUtil.getImgDownloadUri(association.getCover()))
                .apply(GlideUtils.OPTIONS)
                .into(cover);
        associationName.setText(association.getAssociationName());
        establishTime.setText(String.valueOf(association.getEstablishTime()));
    }

}
