package com.example.graduatedesign.adapter.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.R;
import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.utils.GlideUtils;

public class MessageDetailHolder extends RecyclerView.ViewHolder {
    private final View itemView;

    //对方头像
    private final ImageView senderImg;
    //我的头像
    private final ImageView MyImg;
    //对方发送过来的白框背景消息
    private final TextView whiteMsg;
    //我方发送的绿框背景消息
    private final TextView greenMsg;

    public MessageDetailHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        MyImg=itemView.findViewById(R.id.MyImg);
        senderImg=itemView.findViewById(R.id.senderImg);
        whiteMsg=itemView.findViewById(R.id.whiteMsg);
        greenMsg=itemView.findViewById(R.id.greenMsg);
    }

    public static MessageDetailHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_detail, parent, false);
        return new MessageDetailHolder(view);
    }

    public void bind(Message message){
        if (message == null)
            return;

        ImageView imageView;
        /* 根据消息类别设置内容及可见性
        注意，由于MessageDetailViewHolder是可复用的，故会保留上一个绑定数据时的视图设置
        所以不管是发送信息还是接收信息，都要设置对方的相关视图不可见
        */
        //我方发送的消息
        if (message.getType() == 1) {
            whiteMsg.setVisibility(View.GONE);
            senderImg.setVisibility(View.GONE);

            imageView = MyImg;
            greenMsg.setVisibility(View.VISIBLE);
            greenMsg.setText(message.getContent());
            greenMsg.setVisibility(View.VISIBLE);
        }
        //对方发来的消息
        else {
            greenMsg.setVisibility(View.GONE);
            MyImg.setVisibility(View.GONE);

            imageView = senderImg;
            whiteMsg.setVisibility(View.VISIBLE);
            whiteMsg.setText(message.getContent());
            whiteMsg.setVisibility(View.VISIBLE);
        }
        imageView.setVisibility(View.VISIBLE);
        Glide.with(itemView)
                .load(GlideUtils.getImgDownloadUri(message.getSenderPortrait()))
                .apply(GlideUtils.OPTIONS)
                .into(imageView);

    }
}
