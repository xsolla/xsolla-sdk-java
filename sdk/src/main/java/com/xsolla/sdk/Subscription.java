package com.xsolla.sdk;

public class Subscription {
    private int id;
    private int type;
    private int name;
    private int currency;

    public Subscription(int id, int name, int type, int currency) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.currency = currency;
    }

    public int getCurrency() {
        return currency;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getName() {
        return name;
    }
}
