package com.example.graduatedesign.ui.bulletin.adapter.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduatedesign.R;
import com.example.graduatedesign.ui.bulletin.data.SimpleBulletin;

public class SimpleBulletinHolder extends RecyclerView.ViewHolder{
    private final View itemView;

    private TextView txtView;
    private TextView timeView;

    private SimpleBulletinHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView=itemView;

        txtView = itemView.findViewById(R.id.txt);
        timeView = itemView.findViewById(R.id.time);
    }

    public static SimpleBulletinHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_txt_with_time, parent, false);
        return new SimpleBulletinHolder(view);
    }

    public void bind(SimpleBulletin simpleBulletin){
        if (simpleBulletin == null)
            return;

        txtView.setText(simpleBulletin.getTxt());
        timeView.setText(simpleBulletin.getTime());
    }
}
