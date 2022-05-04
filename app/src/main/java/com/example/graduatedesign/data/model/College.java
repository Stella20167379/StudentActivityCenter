package com.example.graduatedesign.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class College {
    @PrimaryKey
    private Integer id;
    private String collegeName;
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
