package com.example.graduatedesign.utils;

import com.example.graduatedesign.association_module.data.Association;
import com.example.graduatedesign.data.model.College;
import com.example.graduatedesign.data.model.Message;
import com.example.graduatedesign.data.model.MyStudentActivity;
import com.example.graduatedesign.personal_module.data.PayRecord;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.student_activity_module.data.Comment;
import com.example.graduatedesign.ui.bulletin.data.SimpleBulletin;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 保存用以转换将list<map>转换为list<pojo>的类型工具
 */
public class GsonConvertTypes {

    /**
     * 活动列表类型
     */
    public static final Type gsonTypeOfActivityList = new TypeToken<ArrayList<MyStudentActivity>>() {
    }.getType();
    /**
     * 用户列表类型
     */
    public static final Type gsonTypeOfUserList = new TypeToken<ArrayList<User>>() {
    }.getType();
    /**
     * 社团列表类型
     */
    public static final Type gsonTypeOfAssociationList = new TypeToken<ArrayList<Association>>() {
    }.getType();
    /**
     * 公告列表类型
     */
    public static final Type gsonTypeOfBulletinList = new TypeToken<ArrayList<SimpleBulletin>>() {
    }.getType();
    /**
     * 支付记录列表类型
     */
    public static final Type gsonTypeOfPayRecordList = new TypeToken<ArrayList<PayRecord>>() {
    }.getType();
    /**
     * 评论列表类型
     */
    public static final Type gsonTypeOfCommentList = new TypeToken<ArrayList<Comment>>() {
    }.getType();
    /**
     * 信息列表类型
     */
    public static final Type gsonTypeOfMessageList = new TypeToken<ArrayList<Message>>() {
    }.getType();

    /**
     * 学校列表类型
     */
    public static final Type gsonTypeOfCollegeList = new TypeToken<ArrayList<College>>() {
    }.getType();
}
