package com.irn.vpn.utils;

import java.io.Serializable;

public class SubscriptionBean implements Serializable {

    String SubName;
    String SKU;
    String price;

    public String getSubName() {
        return SubName;
    }

    public void setSubName(String subName) {
        SubName = subName;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
