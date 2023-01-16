package com.appisoft.iperkz.activity.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView {
    private String displayName;
    private String perkzStatus ="";

    //... other data fields that may be accessible to the UI

    public LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }
    public void setPerkzStatus(String perkzStatusValue) {
        this.perkzStatus = perkzStatusValue;
    }
    String getPerkzStatus() {
        return perkzStatus;
    }
}
