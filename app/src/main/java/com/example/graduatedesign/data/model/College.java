package com.example.graduatedesign.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class College implements Serializable {
    @PrimaryKey
    @SerializedName("id")
    private Integer id;

    @SerializedName("college_name")
    private String collegeName;

    @SerializedName("badge_img")
    private String badgeImg;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getBadgeImg() {
        return badgeImg;
    }

    public void setBadgeImg(String badgeImg) {
        this.badgeImg = badgeImg;
    }
}
