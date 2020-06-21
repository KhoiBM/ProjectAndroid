package com.prm391.project.bingeeproject.Model;

public class Order {
    private String  mProductId;
    private String mProductName;
    private String mQuantity;
    private Double mPrice;
    private String mDiscount;

    public Order() {
    }

    public Order(String mProductId, String mProductName, String mQuantity, Double mPrice, String mDiscount) {
        this.mProductId = mProductId;
        this.mProductName = mProductName;
        this.mQuantity = mQuantity;
        this.mPrice = mPrice;
        this.mDiscount = mDiscount;
    }

    public String getmProductId() {
        return mProductId;
    }

    public void setmProductId(String mProductId) {
        this.mProductId = mProductId;
    }

    public String getmProductName() {
        return mProductName;
    }

    public void setmProductName(String mProductName) {
        this.mProductName = mProductName;
    }

    public String getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(String mQuantity) {
        this.mQuantity = mQuantity;
    }

    public Double getmPrice() {
        return mPrice;
    }

    public void setmPrice(Double mPrice) {
        this.mPrice = mPrice;
    }

    public String getmDiscount() {
        return mDiscount;
    }

    public void setmDiscount(String mDiscount) {
        this.mDiscount = mDiscount;
    }
}
