package com.prm391.project.bingeeproject.Model;

public class Product {
//    private int mID;
    private String mName;
    private String mImage;
    private String mDescription;
    private Double mPrice;
    private String mCategoryID;

    public Product() {
    }

    public Product(String mName, String mImage, String mDescription, Double mPrice, String mCategoryID) {
        this.mName = mName;
        this.mImage = mImage;
        this.mDescription = mDescription;
        this.mPrice = mPrice;
        this.mCategoryID = mCategoryID;
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

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public Double getmPrice() {
        return mPrice;
    }

    public void setmPrice(Double mPrice) {
        this.mPrice = mPrice;
    }

    public String getmCategoryID() {
        return mCategoryID;
    }

    public void setmCategoryID(String mCategoryID) {
        this.mCategoryID = mCategoryID;
    }
}
