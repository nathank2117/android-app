package com.appisoft.iperkz.activity.data.model;

import androidx.annotation.Nullable;

public class RegistrationFormState {
    @Nullable
    private Integer firstnameError;
    @Nullable
    private Integer lastnameError;
    @Nullable
    private Integer emailError;

    private boolean isDataValid;

    public RegistrationFormState(@Nullable Integer firstnameError,
                   @Nullable Integer lastnameError,
                   @Nullable Integer emailError) {
        this.firstnameError = firstnameError;
        this.lastnameError = lastnameError;
        this.emailError = emailError;
        this.isDataValid = false;
    }

    public RegistrationFormState(boolean isDataValid) {
        this.firstnameError = null;
        this.lastnameError = null;
        this.emailError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getFirstnameError() {
        return firstnameError;
    }

    @Nullable
    public Integer getLastnameError() {
        return lastnameError;
    }

    @Nullable
    public Integer getEmailError() {
        return emailError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
