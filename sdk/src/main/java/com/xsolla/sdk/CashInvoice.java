package com.xsolla.sdk;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CashInvoice extends Invoice<CashInvoice> {

    protected String datetime;
    protected BigDecimal userAmount;
    protected String userCurrency;
    protected BigDecimal transferAmount;
    protected String transferCurrency;
    protected Integer pid;
    protected Integer geotype;

    public CashInvoice getThis() {
        return this;
    }

    public String getDatetime() {
        return datetime;
    }

    public CashInvoice setDatetime(String datetime) {
        this.datetime = datetime;
        return this;
    }

    public BigDecimal getUserAmount() {
        return userAmount;
    }

    public CashInvoice setUserAmount(BigDecimal userAmount) {
        this.userAmount = userAmount.setScale(2, RoundingMode.FLOOR);
        return this;
    }

    public String getUserCurrency() {
        return userCurrency;
    }

    public CashInvoice setUserCurrency(String userCurrency) {
        this.userCurrency = userCurrency;
        return this;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public CashInvoice setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount.setScale(2, RoundingMode.FLOOR);
        return this;
    }

    public String getTransferCurrency() {
        return transferCurrency;
    }

    public CashInvoice setTransferCurrency(String transferCurrency) {
        this.transferCurrency = transferCurrency;
        return this;
    }

    public Integer getPid() {
        return pid;
    }

    public CashInvoice setPid(Integer pid) {
        this.pid = pid;
        return this;
    }

    public Integer getGeotype() {
        return geotype;
    }

    public CashInvoice setGeotype(Integer geotype) {
        this.geotype = geotype;
        return this;
    }
}
