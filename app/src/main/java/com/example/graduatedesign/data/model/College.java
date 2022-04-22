package com.example.graduatedesign.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class College {
    @PrimaryKey
    private Integer id;
    private String college_name;
    private String badge_img;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCollege_name() {
        return college_name;
    }

    public void setCollege_name(String college_name) {
        this.college_name = college_name;
    }

    public String getBadge_img() {
        return badge_img;
    }

    public void setBadge_img(String badge_img) {
        this.badge_img = badge_img;
    }

    @Override
    public String toString() {
        return "College{" +
                "id=" + id +
                ", college_name='" + college_name + '\'' +
                ", badge_img='" + badge_img + '\'' +
                '}';
    }
}
