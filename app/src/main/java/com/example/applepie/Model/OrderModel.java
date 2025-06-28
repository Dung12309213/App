package com.example.applepie.Model;

import java.io.Serializable;
import java.util.Date;

public class OrderModel implements Serializable {
    private String id;
    private String userid;
    private Date purchasedate;
    private String discountid;
    private String status;
    private String paymentMethod;
    private double total;
    private String firstProductImageUrl; // URL ảnh sản phẩm đầu tiên
    private int totalItemCount;
    public OrderModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Date getPurchasedate() {
        return purchasedate;
    }

    public void setPurchasedate(Date purchasedate) {
        this.purchasedate = purchasedate;
    }

    public String getDiscountid() {
        return discountid;
    }

    public void setDiscountid(String discountid) {
        this.discountid = discountid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
    public String getFirstProductImageUrl() {
        return firstProductImageUrl;
    }

    public void setFirstProductImageUrl(String firstProductImageUrl) {
        this.firstProductImageUrl = firstProductImageUrl;
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public void setTotalItemCount(int totalItemCount) {
        this.totalItemCount = totalItemCount;
    }
}
