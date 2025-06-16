package com.example.applepie.Model;

public class AddressModel {
    private final String fullName;
    private final String address;
    private final String phone;
    private boolean isSelected;

    public AddressModel(String fullName, String address, String phone, boolean isSelected) {
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.isSelected = isSelected;
    }

    public String getFullName() { return fullName; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
