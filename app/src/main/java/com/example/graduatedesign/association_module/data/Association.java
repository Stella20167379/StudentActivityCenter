package com.example.graduatedesign.association_module.data;

import java.io.Serializable;

public class Association implements Serializable {
    private int id;
    private String associationName;
    private String establishTime;
    private String coverImg;
    private String introduction;
    private int collegeId;
    /**
     * 当前用户与当前社团的关系，1-普通用户，2-社团管理员
     */
    private int relState;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAssociationName() {
        return associationName;
    }

    public void setAssociationName(String associationName) {
        this.associationName = associationName;
    }

    public String getEstablishTime() {
        return establishTime;
    }

    public void setEstablishTime(String establishTime) {
        this.establishTime = establishTime;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(int collegeId) {
        this.collegeId = collegeId;
    }

    public int getRelState() {
        return relState;
    }

    public void setRelState(int relState) {
        this.relState = relState;
    }
}
