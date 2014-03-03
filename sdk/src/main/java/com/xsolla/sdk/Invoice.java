package com.xsolla.sdk;

import java.math.BigDecimal;
import java.math.RoundingMode;

abstract public class Invoice <T extends Invoice<T>> {

    protected Long id;
    protected Long idXsolla;
    protected BigDecimal amount;
    protected String currency;
    protected String v1;
    protected String v2;
    protected String v3;

    abstract public T getThis();

    public Long getId() {
        return id;
    }

    public T setId(Long id) {
        this.id = id;
        return getThis();
    }

    public Long getIdXsolla() {
        return idXsolla;
    }

    public T setIdXsolla(Long idXsolla) {
        this.idXsolla = idXsolla;
        return getThis();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public T setAmount(BigDecimal amount) {
        this.amount = amount.setScale(2, RoundingMode.FLOOR);
        return getThis();
    }

    public String getCurrency() {
        return currency;
    }

    public T setCurrency(String currency) {
        this.currency = currency;
        return getThis();
    }

    public String getV1() {
        return v1;
    }

    public T setV1(String v1) {
        this.v1 = v1;
        return getThis();
    }

    public String getV2() {
        return v2;
    }

    public T setV2(String v2) {
        this.v2 = v2;
        return getThis();
    }

    public String getV3() {
        return v3;
    }

    public T setV3(String v3) {
        this.v3 = v3;
        return getThis();
    }
}
