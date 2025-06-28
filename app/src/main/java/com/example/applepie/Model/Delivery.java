package com.example.applepie.Model;

import java.util.Date;

public class Delivery {
    private String id;
    private String orderId;
    private String deliveryStatus;
    private String shippingProvider;
    private String deliveryName;
    private String deliveryPhone;
    private String addressStreet;
    private String addressWard;
    private String addressDistrict;
    private String addressProvince;
    private Date delivery_date_estimated;
    private Date delivery_date_actual;

    public Delivery() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getShippingProvider() {
        return shippingProvider;
    }

    public void setShippingProvider(String shippingProvider) {
        this.shippingProvider = shippingProvider;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public String getAddressWard() {
        return addressWard;
    }

    public void setAddressWard(String addressWard) {
        this.addressWard = addressWard;
    }

    public String getAddressDistrict() {
        return addressDistrict;
    }

    public void setAddressDistrict(String addressDistrict) {
        this.addressDistrict = addressDistrict;
    }

    public String getAddressProvince() {
        return addressProvince;
    }

    public void setAddressProvince(String addressProvince) {
        this.addressProvince = addressProvince;
    }

    public Date getDelivery_date_estimated() {
        return delivery_date_estimated;
    }

    public void setDelivery_date_estimated(Date delivery_date_estimated) {
        this.delivery_date_estimated = delivery_date_estimated;
    }

    public Date getDelivery_date_actual() {
        return delivery_date_actual;
    }

    public void setDelivery_date_actual(Date delivery_date_actual) {
        this.delivery_date_actual = delivery_date_actual;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }
}
