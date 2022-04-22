package com.example.graduatedesign;


/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private int userId;
    private String schoolName;
    private String email;
    private String portrait;
    private Integer credentialInfoId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public Integer getCredentialInfoId() {
        return credentialInfoId;
    }

    public void setCredentialInfoId(Integer credentialInfoId) {
        this.credentialInfoId = credentialInfoId;
    }

}