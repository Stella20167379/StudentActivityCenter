package com.example.graduatedesign.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Message implements Serializable {
    //消息id
    @PrimaryKey
    private int id;
    //是 自己发送-1 的还是 接收到的-2，只在客户端有效
    private int type;
    //发送人名称
    private String sender;
    //发送人头像
    private String senderPortrait;
    //发送人id
    private int senderId;
    //消息内容
    private String content;
    //发送时间
    private String sendTime;
    //是否是入会申请 1-普通消息，- 2-入会申请
    private int msgType;
    //是否已读
    private int state;
    //作为入会申请时，是否通过
    private boolean applyState;
    //接收人
    private int receiverId;
    /**
     * 记录社团id
     */
    private Boolean associationId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getSenderPortrait() {
        return senderPortrait;
    }

    public void setSenderPortrait(String senderPortrait) {
        this.senderPortrait = senderPortrait;
    }

    public boolean isApplyState() {
        return applyState;
    }

    public void setApplyState(boolean applyState) {
        this.applyState = applyState;
    }

    public Boolean getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Boolean associationId) {
        this.associationId = associationId;
    }
}
