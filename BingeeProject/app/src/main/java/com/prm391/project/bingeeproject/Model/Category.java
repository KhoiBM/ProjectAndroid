package com.prm391.project.bingeeproject.Model;

public class Category {
    private String mName;
    private String mImage;

    public Category() {

    }

    public Category(String mName, String mImage) {
        this.mName = mName;
        this.mImage = mImage;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }
}
