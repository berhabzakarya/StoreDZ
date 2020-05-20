package com.berhabzakarya.storedz.Model;

public class AdminOrder {
    private String totalAmount,name,phone,address,date,time,city,state;
    AdminOrder(){};

    public AdminOrder(String totalAmount, String name, String phone, String address, String date, String time, String city, String state) {
        this.totalAmount = totalAmount;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.date = date;
        this.time = time;
        this.city = city;
        this.state = state;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
