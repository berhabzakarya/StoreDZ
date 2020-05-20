package com.berhabzakarya.storedz.Model;

public class Users {
    private String phone,name,password,address,image;

    public Users(){}

    public Users(String phone, String name, String password, String address, String image) {
        this.phone = phone;
        this.name = name;
        this.password = password;
        this.address = address;
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
