package com.prm391.project.bingeeproject.Model;

public class Order {
    private String  mProductId;
    private String mProductName;
    private int mQuantity;
    private Double mPrice;

    public Order() {
    }

    public Order(String mProductId, String mProductName, int mQuantity, Double mPrice) {
        this.mProductId = mProductId;
        this.mProductName = mProductName;
        this.mQuantity = mQuantity;
        this.mPrice = mPrice;
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

    public int getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }

    public Double getmPrice() {
        return mPrice;
    }

    public void setmPrice(Double mPrice) {
        this.mPrice = mPrice;
    }

    @Override
    public String toString() {
        return "Order{" +
                "mProductId='" + mProductId + '\'' +
                ", mProductName='" + mProductName + '\'' +
                ", mQuantity='" + mQuantity + '\'' +
                ", mPrice=" + mPrice +
                '}';
    }
}
