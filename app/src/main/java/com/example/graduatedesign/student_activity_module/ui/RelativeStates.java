package com.example.graduatedesign.student_activity_module.ui;

public interface RelativeStates {

    //////////////////////////////////////////////////////////////////////

    /**
     * 活动报名的几种状态
     * 未开始状态
     */
    int SignNotStartState = 1;

    /**
     * 活动报名的几种状态
     * 可报名状态
     */
    int SignAvailableState = 2;

    /**
     * 活动报名的几种状态
     * 不可报名状态
     */
    int SignDisableState = 3;

    //////////////////////////////////////////////////////////////////////

    /**
     * 活动举行的几种状态
     * 未开始状态
     */
    int ActivityNotStartState = 1;

    /**
     * 活动举行的几种状态
     * 进行中状态
     */
    int ActivityStartState = 2;

    /**
     * 活动举行的几种状态
     * 已结束状态
     */
    int ActivityEndState = 3;

    //////////////////////////////////////////////////////////////////////

    /**
     * 用户与活动关联的几种状态
     * 未报名状态
     */
    int RelNoneState = 0;

    /**
     * 用户与活动关联的几种状态
     * 管理员状态
     */
    int RelAdminState = -1;

    /**
     * 用户与活动关联的几种状态
     * 未签到状态
     */
    int RelNotCheckInState = 1;

    /**
     * 用户与活动关联的几种状态
     * 未签退状态
     */
    int RelNotCheckOutState = 2;

    /**
     * 用户与活动关联的几种状态
     * 已签退状态
     */
    int RelAlreadyCheckOutState = 3;

    /////////////////////////////////////////////////////////////////////////

    /**
     * 按钮区分事件所用
     * 报名状态
     */
    int BtnSignState = 1;

    /**
     * 按钮区分事件所用
     * 管理员自动完成报名、签到签退步骤，此状态是为了打开相机扫描参与者的二维码
     * 活动管理员 + 可签到签退状态
     */
    int BtnAdminState = 2;
    /**
     * 按钮区分事件所用
     * 活动参与人员 + 可签到状态
     */
    int BtnCheckInState = 3;
    /**
     * 按钮区分事件所用
     * 活动参与人员 + 可签退状态
     */
    int BtnCheckOutState = 4;

    //////////////////////////////////////////////////////////////////////////

    /**
     * 生成收费二维码操作
     */
    int ActionPay = 1;

    /**
     * 生成签到二维码操作
     */
    int ActionCheckIn = 2;

    /**
     * 生成签退二维码操作
     */
    int ActionCheckOut = 3;

    //////////////////////////////////////////////////////////////////////////

    /**
     * 分两种方式跳转到添加活动页面
     * 此为未设置该参数，系统默认为0时
     */
    int FromNone = 0;

    /**
     * 分两种方式跳转到添加活动页面
     * 此为首页直接进入
     */
    int FromHomeFragment = 1;

    /**
     * 分两种方式跳转到添加活动页面
     * 此为社团详情页面进入
     */
    int FromAssociationFragment = 2;

    //////////////////////////////////////////////////////////////////////////

}
