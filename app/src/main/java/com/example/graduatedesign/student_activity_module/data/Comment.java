package com.example.graduatedesign.student_activity_module.data;


public class Comment {

    private int userId;
    private int activityId;
    private String activityName;
    private String senderImg;
    private String content;
    private double score;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getSenderImg() {
        return senderImg;
    }

    public void setSenderImg(String senderImg) {
        this.senderImg = senderImg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    @Override
    public String toString() {
        return "{" +
                "\"userId\":" + userId +
                ",\"activityId\":" + activityId +
                ",\"activityName\":\"" + activityName + "\"" +
                ",\"senderImg\":\"" + senderImg + "\"" +
                ",\"content\":\"" + content + "\"" +
                ",\"score\":" + score +
                '}';
    }
}
