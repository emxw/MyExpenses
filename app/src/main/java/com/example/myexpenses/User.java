package com.example.myexpenses;

public class User {
    public String username, mobileNumber, email;

    public User(){

    }

    public User(String username, String mobileNumber, String email){
        this.username = username;
        this.mobileNumber = mobileNumber;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
