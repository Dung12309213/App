package com.example.applepie.Model;

import java.util.Date;

public class User {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String gender;
    private Date dob;

    public User() {
    }

    public User(String name, String phone, String email) { // LOẠI BỎ PASSWORD KHỎI HÀM TẠO
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public User(String id, String name, String phone, String email, String gender, Date dob) { // LOẠI BỎ PASSWORD
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }
}