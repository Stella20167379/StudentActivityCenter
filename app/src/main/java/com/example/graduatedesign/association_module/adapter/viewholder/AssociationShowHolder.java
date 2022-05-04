package com.example.graduatedesign.association_module.adapter.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.R;
import com.example.graduatedesign.association_module.data.Association;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GlideUtils;

public class AssociationShowHolder extends RecyclerView.ViewHolder {
    private final View itemView;

    private final ImageView cover;
    private final TextView name;
    private final TextView introduction;

    private AssociationShowHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView=itemView;

        cover=itemView.findViewById(R.id.cover);
        name=itemView.findViewById(R.id.txtName);
        introduction=itemView.findViewById(R.id.introduction_title);
    }

    public static AssociationShowHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_association_show, parent, false);
        return new AssociationShowHolder(view);
    }

    public void bind(Association association){
        if (association == null)
            return;

        Glide.with(itemView)
                .load(DataUtil.getImgDownloadUri(association.getCover()))
                .apply(GlideUtils.OPTIONS)
                .into(cover);
        name.setText(association.getAssociationName());
        introduction.setText(association.getIntroduction());
    }
}
