package com.example.graduatedesign.personal_module.data;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private int schoolId;
    private String schoolName;
    private String nickname;
    private String portrait;
//    T-女,F-男
    private boolean sex;
    //学号/工号id
    private Integer credentialInfoId;
    //学号
    private String credentialNum;
    private String role;
    private String email;
    private String majorClass;
    /**
     * 是否拥有管理员身份，用来控制某些特定视图组件的显示与否
     */
    private boolean isAssociationAdmin;
    /**
     * 通用：0-无关联
     * 作社团成员标记时：1-普通成员，2-管理员
     * 作活动成员标记时：-1-活动管理员，1-未签到，2-已签到未签退，3-已签退
     */
    private int state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getCredentialNum() {
        return credentialNum;
    }

    public void setCredentialNum(String credentialNum) {
        this.credentialNum = credentialNum;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMajorClass() {
        return majorClass;
    }

    public void setMajorClass(String majorClass) {
        this.majorClass = majorClass;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Integer getCredentialInfoId() {
        return credentialInfoId;
    }

    public void setCredentialInfoId(Integer credentialInfoId) {
        this.credentialInfoId = credentialInfoId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isAssociationAdmin() {
        return isAssociationAdmin;
    }

    public void setAssociationAdmin(boolean associationAdmin) {
        isAssociationAdmin = associationAdmin;
    }
}
