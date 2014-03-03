package com.xsolla.sdk;

public class User {
    protected String v1;
    protected String v2;
    protected String v3;
    protected String email;
    protected String phone;
    protected String userIP;

    public User(String v1) {
        this.v1 = v1;
    }

    public String getV1() {
        return v1;
    }

    public User setV1(String v1) {
        this.v1 = v1;
        return this;
    }

    public String getV2() {
        return v2;
    }

    public User setV2(String v2) {
        this.v2 = v2;
        return this;
    }

    public String getV3() {
        return v3;
    }

    public User setV3(String v3) {
        this.v3 = v3;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getUserIP() {
        return userIP;
    }

    public User setUserIP(String userIP) {
        this.userIP = userIP;
        return this;
    }
}
