package com.example.tourmate;

public class User {
    String userName, userEmail, userPhone, userImage;

    public User() {
    }

    public User(String userImage, String userName, String userEmail, String userPhone) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserImage() {
        return userImage;
    }
}
