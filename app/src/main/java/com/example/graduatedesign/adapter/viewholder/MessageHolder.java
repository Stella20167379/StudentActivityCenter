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
import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.utils.GlideUtils;

public class MessageHolder extends RecyclerView.ViewHolder {

    private final View itemView;

    //发送人头像
    private final ImageView portrait;
    //发送人姓名
    private final TextView senderName;
    //发送日期
    private final TextView dateTxt;
    //发送内容
    private final TextView content;

    public MessageHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView=itemView;

        content = itemView.findViewById(R.id.content);
        portrait = itemView.findViewById(R.id.senderImg);
        senderName = itemView.findViewById(R.id.senderName);
        dateTxt = itemView.findViewById(R.id.date);

    }

    public static MessageHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_show, parent, false);
        return new MessageHolder(view);
    }

    public void bind(Message message){
        if (message == null)
            return;

        int resourceId = R.mipmap.ic_launcher;
        
        Glide.with(itemView)
                .load(resourceId)
                .apply(GlideUtils.OPTIONS)
                .into(portrait);
        senderName.setText(message.getSender());
        dateTxt.setText(message.getSendTime().toString());
        content.setText(message.getContent());
    }

}
