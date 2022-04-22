package com.example.graduatedesign.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private Integer principalError;
    @Nullable
    private Integer credentialError;
    private boolean isDataValid;

    //用户输入有不合法时
    LoginFormState(@Nullable Integer princpalError, @Nullable Integer credentialError) {
        this.principalError = princpalError;
        this.credentialError = credentialError;
        this.isDataValid = false;
    }

    //用户输入都合法时
    LoginFormState(boolean isDataValid) {
        this.principalError = null;
        this.credentialError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getPrincipalError() {
        return principalError;
    }

    @Nullable
    Integer getCredentialError() {
        return credentialError;
    }

    boolean isDataValid() {
        return isDataValid;
    }

    public void setPrincipalError(@Nullable Integer principalError) {
        this.principalError = principalError;
    }

    public void setCredentialError(@Nullable Integer credentialError) {
        this.credentialError = credentialError;
    }

    public void setDataValid(boolean dataValid) {
        isDataValid = dataValid;
    }
}