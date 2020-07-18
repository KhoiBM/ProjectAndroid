package com.prm391.project.bingeeproject.Model;

import java.util.List;

public class Request {
    private String mPhoneUser;
    private String mShippingToPhone;
    private String mShippingToFullname;
    private String mShippingToAddress;
    private Double mTotal;
    private String mStatus;
    private String mDateOrder;
    private String mPaymentMethod;
    private List<Order> mOrders;

    public Request() {
    }

    public Request(String mPhoneUser, String mShippingToPhone, String mShippingToFullname, String mShippingToAddress, Double mTotal, String mStatus, String mDateOrder, String mPaymentMethod, List<Order> mOrders) {
        this.mPhoneUser = mPhoneUser;
        this.mShippingToPhone = mShippingToPhone;
        this.mShippingToFullname = mShippingToFullname;
        this.mShippingToAddress = mShippingToAddress;
        this.mTotal = mTotal;
        this.mStatus = mStatus;
        this.mDateOrder = mDateOrder;
        this.mPaymentMethod = mPaymentMethod;
        this.mOrders = mOrders;
    }

    public String getmPhoneUser() {
        return mPhoneUser;
    }

    public void setmPhoneUser(String mPhoneUser) {
        this.mPhoneUser = mPhoneUser;
    }

    public String getmShippingToPhone() {
        return mShippingToPhone;
    }

    public void setmShippingToPhone(String mShippingToPhone) {
        this.mShippingToPhone = mShippingToPhone;
    }

    public String getmShippingToFullname() {
        return mShippingToFullname;
    }

    public void setmShippingToFullname(String mShippingToFullname) {
        this.mShippingToFullname = mShippingToFullname;
    }

    public String getmShippingToAddress() {
        return mShippingToAddress;
    }

    public void setmShippingToAddress(String mShippingToAddress) {
        this.mShippingToAddress = mShippingToAddress;
    }

    public Double getmTotal() {
        return mTotal;
    }

    public void setmTotal(Double mTotal) {
        this.mTotal = mTotal;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmDateOrder() {
        return mDateOrder;
    }

    public void setmDateOrder(String mDateOrder) {
        this.mDateOrder = mDateOrder;
    }

    public String getmPaymentMethod() {
        return mPaymentMethod;
    }

    public void setmPaymentMethod(String mPaymentMethod) {
        this.mPaymentMethod = mPaymentMethod;
    }

    public List<Order> getmOrders() {
        return mOrders;
    }

    public void setmOrders(List<Order> mOrders) {
        this.mOrders = mOrders;
    }
}

