package com.example.graduatedesign.data.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MyStudentActivity implements Cloneable, Serializable {
    private int id;
    //活动标题
    private String title;
    //活动报名起始日期
    private String signStart;
    //活动报名终止日期
    private String signEnd;
    //活动举行的起始日期
    private String activityStart;
    //活动举行的截止日期
    private String activityEnd;
    //举办活动的社团id
    private int associationId;
    //举办活动的社团名称
    private String associationName;
    //活动封面图片
    private String coverImg;
    //活动地点
    private String location;
    //活动介绍文案
    private String introduction;
    /**
     * 学校id
     */
    private Integer collegeId;

    /**
     * 参与状态，-1-活动管理员，0-无关联,1-未签到，2-未签退，3-已签退
     */
    private Integer participantState;
    //是否付费活动,默认为不收费
    private boolean isPayNeed = false;
    //单人收费金额
    private Integer chargeAmount;
    //资金用途
    private String fundUse;
    //支付宝账号
    private String alipayAccount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSignStart() {
        return signStart;
    }

    public void setSignStart(String signStart) {
        this.signStart = signStart;
    }

    public String getSignEnd() {
        return signEnd;
    }

    public void setSignEnd(String signEnd) {
        this.signEnd = signEnd;
    }

    public String getActivityStart() {
        return activityStart;
    }

    public void setActivityStart(String activityStart) {
        this.activityStart = activityStart;
    }

    public String getActivityEnd() {
        return activityEnd;
    }

    public void setActivityEnd(String activityEnd) {
        this.activityEnd = activityEnd;
    }

    public String getAssociationName() {
        return associationName;
    }

    public void setAssociationName(String associationName) {
        this.associationName = associationName;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Integer getParticipantState() {
        return participantState;
    }

    public void setParticipantState(Integer participantState) {
        this.participantState = participantState;
    }

    public boolean isPayNeed() {
        return isPayNeed;
    }

    public void setPayNeed(boolean payNeed) {
        isPayNeed = payNeed;
    }

    public String getFundUse() {
        return fundUse;
    }

    public void setFundUse(String fundUse) {
        this.fundUse = fundUse;
    }

    public String getAlipayAccount() {
        return alipayAccount;
    }

    public void setAlipayAccount(String alipayAccount) {
        this.alipayAccount = alipayAccount;
    }

    public Integer getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(Integer chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public int getAssociationId() {
        return associationId;
    }

    public void setAssociationId(int associationId) {
        this.associationId = associationId;
    }

    public Integer getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Integer collegeId) {
        this.collegeId = collegeId;
    }

    /**
     * 使用继承的clone方法实现浅克隆
     */
    public MyStudentActivity clone() {
        try {
            return (MyStudentActivity) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用序列化技术实现深克隆
     */
    public MyStudentActivity deepClone() {
        try {
            //将对象写入流中
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(this);//此处写this不写bao

            //将对象从流中取出
            ByteArrayInputStream bis = new ByteArrayInputStream(bao.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (MyStudentActivity) ois.readObject();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
