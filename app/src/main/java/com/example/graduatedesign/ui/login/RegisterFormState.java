package com.example.graduatedesign.ui.login;

/**
 * 集中保存注册输入框的当前状态
 */
public class RegisterFormState {
    //使用Integer传入资源id也可以的
    private String emailError;
    private String PassError;
    private String nicknameError;
    private String verifyCodeError;
    private boolean isValid;

    //用户输入有不合法时
    public RegisterFormState(String emailError, String passError, String nicknameError, String verifyCodeError) {
        this.nicknameError = nicknameError;
        isValid=false;
        this.emailError = emailError;
        PassError = passError;
        this.verifyCodeError = verifyCodeError;
    }

    //用户输入都合法时
    public RegisterFormState(boolean isValid){
        this.isValid=isValid;
        emailError=null;
        PassError=null;
        nicknameError=null;
        verifyCodeError=null;
    }

    public String getEmailError() {
        return emailError;
    }

    public String getPassError() {
        return PassError;
    }

    public String getVerifyCodeError() {
        return verifyCodeError;
    }

    public boolean getValid() {
        return isValid;
    }

    public String getNicknameError() {
        return nicknameError;
    }

    public void setEmailError(String emailError) {
        this.emailError = emailError;
    }

    public void setPassError(String passError) {
        PassError = passError;
    }

    public void setNicknameError(String nicknameError) {
        this.nicknameError = nicknameError;
    }

    public void setVerifyCodeError(String verifyCodeError) {
        this.verifyCodeError = verifyCodeError;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

}
