package com.prm391.project.bingeeproject.Model;

public class Product {
    private String mName;
    private String mImage;
    private String mDescription;
    private Double mPrice;
    private String mMenuID;
    private  String mDiscount;

    public Product() {
    }

    public Product(String mName, String mImage, String mDescription, Double mPrice, String mMenuID, String mDiscount) {
        this.mName = mName;
        this.mImage = mImage;
        this.mDescription = mDescription;
        this.mPrice = mPrice;
        this.mMenuID = mMenuID;
        this.mDiscount = mDiscount;
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

    public String getmMenuID() {
        return mMenuID;
    }

    public void setmMenuID(String mMenuID) {
        this.mMenuID = mMenuID;
    }

    public String getmDiscount() {
        return mDiscount;
    }

    public void setmDiscount(String mDiscount) {
        this.mDiscount = mDiscount;
    }
}
