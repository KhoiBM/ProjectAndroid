package com.prm391.project.bingeeproject.Model;

public class User {
    private String mName;
    private String mPassword;
    private String mPhone;
    private String mAddress;
    private String mEmail;
    private boolean mGender;

    public User() {
    }

    public User(String mName, String mPassword, String mPhone, String mAddress, String mEmail, boolean mGender) {
        this.mName = mName;
        this.mPassword = mPassword;
        this.mPhone = mPhone;
        this.mAddress = mAddress;
        this.mEmail = mEmail;
        this.mGender = mGender;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public boolean ismGender() {
        return mGender;
    }

    public void setmGender(boolean mGender) {
        this.mGender = mGender;
    }

}
