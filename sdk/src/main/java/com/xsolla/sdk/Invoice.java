package com.xsolla.sdk;

import java.math.BigDecimal;

public class Invoice {

    protected BigDecimal virtualCurrencyAmount;
    protected BigDecimal amount;
    protected String currency;
    protected Long id;

    public BigDecimal getVirtualCurrencyAmount() {
        return virtualCurrencyAmount;
    }

    public Invoice setVirtualCurrencyAmount(BigDecimal virtualCurrencyAmount) {
        this.virtualCurrencyAmount = virtualCurrencyAmount.setScale(2, BigDecimal.ROUND_CEILING);
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Invoice setAmount(BigDecimal amount) {
        this.amount = amount.setScale(2, BigDecimal.ROUND_CEILING);
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public Invoice setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Invoice setId(Long id) {
        this.id = id;
        return this;
    }
}
