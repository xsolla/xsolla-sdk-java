package com.xsolla.sdk;

public class Subscription {

    protected int id;
    protected String type;
    protected String name;
    protected String currency;

    public Subscription(int id, String name, String type, String currency) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
