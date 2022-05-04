package com.example.graduatedesign.student_activity_module.ui.detail;

public class ActivityRelativeStates {
    //////////////////////////////////////////////////////////////////////

    /**
     * 活动报名的几种状态
     * 未开始状态
     */
    public static final int SignNotStartState=1;

    /**
     * 活动报名的几种状态
     * 可报名状态
     */
    public static final int SignAvailableState=2;

    /**
     * 活动报名的几种状态
     * 不可报名状态
     */
    public static final int SignDisableState=3;

    //////////////////////////////////////////////////////////////////////

    /**
     * 活动举行的几种状态
     * 未开始状态
     */
    public static final int ActivityNotStartState=1;

    /**
     * 活动举行的几种状态
     * 进行中状态
     */
    public static final int ActivityStartState=2;

    /**
     * 活动举行的几种状态
     * 已结束状态
     */
    public static final int ActivityEndState=3;

    //////////////////////////////////////////////////////////////////////

    /**
     * 用户与活动关联的几种状态
     * 未报名状态
     */
    public static final int RelNoneState=0;

    /**
     * 用户与活动关联的几种状态
     * 管理员状态
     */
    public static final int RelAdminState=-1;

    /**
     * 用户与活动关联的几种状态
     * 未签到状态
     */
    public static final int RelNotCheckInState=1;

    /**
     * 用户与活动关联的几种状态
     * 未签退状态
     */
    public static final int RelNotCheckOutState=2;

    /**
     * 用户与活动关联的几种状态
     * 已签退状态
     */
    public static final int RelAlreadyCheckOutState=3;

    /////////////////////////////////////////////////////////////////////////

    /**
     * 按钮区分事件所用
     * 报名状态
     */
    public static final int BtnSignState =1;

    /**
     * 按钮区分事件所用
     * 活动管理员 + 可签到签退状态
     */
    public static final int BtnAdminState =2;
    /**
     * 按钮区分事件所用
     * 活动参与人员 + 可签到状态
     */
    public static final int BtnCheckInState =3;
    /**
     * 按钮区分事件所用
     * 活动参与人员 + 可签退状态
     */
    public static final int BtnCheckOutState =4;

    //////////////////////////////////////////////////////////////////////////
}
