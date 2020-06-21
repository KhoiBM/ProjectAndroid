package com.prm391.project.bingeeproject.Model;

import java.util.List;

public class Request {
    private String mPhone;
    private String mName;
    private String mAddress;
    private String mTotal;
    private String mStatus;
    private List<Order> mOrders;

    public Request() {
    }

    public Request(String mPhone, String mName, String mAddress, String mTotal, String mStatus, List<Order> mOrders) {
        this.mPhone = mPhone;
        this.mName = mName;
        this.mAddress = mAddress;
        this.mTotal = mTotal;
        this.mStatus = mStatus;
        this.mOrders = mOrders;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getmTotal() {
        return mTotal;
    }

    public void setmTotal(String mTotal) {
        this.mTotal = mTotal;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public List<Order> getmOrders() {
        return mOrders;
    }

    public void setmOrders(List<Order> mOrders) {
        this.mOrders = mOrders;
    }
}
