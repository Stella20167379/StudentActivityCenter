package com.example.graduatedesign.ui.login;

public class ForgetPassFormState {
    private Integer old1Error;
    private Integer new1Error;
    private Integer confirmError;
    private boolean isValid;

    public ForgetPassFormState(Integer old1Error, Integer new1Error, Integer confirmError) {
        isValid = false;
        this.old1Error = old1Error;
        this.new1Error = new1Error;
        this.confirmError = confirmError;
    }

    public ForgetPassFormState(boolean isValid) {
        this.isValid = isValid;
        old1Error = null;
        new1Error = null;
        confirmError = null;
    }

    public Integer getOld1Error() {
        return old1Error;
    }

    public Integer getNew1Error() {
        return new1Error;
    }

    public Integer getConfirmError() {
        return confirmError;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setOld1Error(Integer old1Error) {
        this.old1Error = old1Error;
    }

    public void setNew1Error(Integer new1Error) {
        this.new1Error = new1Error;
    }

    public void setConfirmError(Integer confirmError) {
        this.confirmError = confirmError;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
