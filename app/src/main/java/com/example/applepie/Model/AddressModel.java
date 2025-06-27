package com.example.applepie.Model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class AddressModel implements Serializable {
    private String addressid;
    private String name;
    private String phone;
    private String province;
    private String district;
    private String ward;
    private String street;
    private boolean defaultCheck;

    public AddressModel() {
    }

    public AddressModel(String addressid, String name, String phone, String province, String district, String ward, String street, boolean defaultCheck) {
        this.addressid = addressid;
        this.name = name;
        this.phone = phone;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.street = street;
        this.defaultCheck = defaultCheck;
    }

    public String getAddressid() {
        return addressid;
    }

    public void setAddressid(String addressid) {
        this.addressid = addressid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public boolean isDefaultCheck() {
        return defaultCheck;
    }

    public void setDefaultCheck(boolean defaultCheck) {
        this.defaultCheck = defaultCheck;
    }

    @NonNull
    @Override
    public String toString() {
        String addressString = "Người nhận: " + name
                + "\nSố điện thoại: " + phone
                + "\nĐịa chỉ: " + (street != null ? street + ", " : "")
                + (ward != null ? ward + ", " : "")
                + (district != null ? district + ", " : "")
                + (province != null ? province : "");
        return addressString;
    }
}
