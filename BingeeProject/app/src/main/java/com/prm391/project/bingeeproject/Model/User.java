package com.prm391.project.bingeeproject.Model;

import java.util.Date;

public class User {
    private String mFullName;
    private String mPassword;
    private String mPhone;
    private String mDOB;
    private String mAddress;
    private String mEmail;
    private boolean mGender;

    public User() {
    }

    public User(String mFullName, String mPassword, String mPhone, String mDOB, String mAddress, String mEmail, boolean mGender) {
        this.mFullName = mFullName;
        this.mPassword = mPassword;
        this.mPhone = mPhone;
        this.mDOB = mDOB;
        this.mAddress = mAddress;
        this.mEmail = mEmail;
        this.mGender = mGender;
    }

    public String getmFullName() {
        return mFullName;
    }

    public void setmFullName(String mFullName) {
        this.mFullName = mFullName;
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

    public String getmDOB() {
        return mDOB;
    }

    public void setmDOB(String mDOB) {
        this.mDOB = mDOB;
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
