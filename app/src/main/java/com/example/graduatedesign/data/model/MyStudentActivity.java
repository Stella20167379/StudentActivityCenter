package com.example.graduatedesign.data.model;

public class MyStudentActivity {
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
    //活动人数上限
    private int sumLimit;
    //举办活动的社团名称
    private String associationName;
    //活动封面图片
    private String coverImg;
    //活动地点
    private String location;
    //活动介绍文案
    private String introduction;
    //负责人id
    private int adminId;

    /**
     * 参与状态，-1-活动管理员，0-无关联,1-未签到，2-未签退，3-已签退
     */
    private Integer participantState;
    //是否付费活动
    private boolean isPayNeed;
    //单人收费金额
    private int chargeAmount;
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

    public int getSumLimit() {
        return sumLimit;
    }

    public void setSumLimit(int sumLimit) {
        this.sumLimit = sumLimit;
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

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
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

    public int getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(int chargeAmount) {
        this.chargeAmount = chargeAmount;
    }
}
