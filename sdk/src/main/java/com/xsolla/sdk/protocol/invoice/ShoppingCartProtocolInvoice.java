package com.xsolla.sdk.protocol.invoice;

import java.math.BigDecimal;
import java.util.*;

public class ShoppingCartProtocolInvoice {

    protected long xsollaPaymentId;
    protected BigDecimal amount;
    protected String v1;
    protected String v2;
    protected String v3;
    protected String currency;
    protected Date datetime;
    protected boolean dryRun;
    protected BigDecimal userAmount;
    protected String userCurrency;
    protected BigDecimal transferAmount;
    protected String transferCurrency;
    protected Integer pid;
    protected Integer geotype;

    private Map<String, List<String>> difference;

    /**
     * @param xsollaPaymentId Unique
     * @param amount
     * @param v1
     * @param currency
     * @param datetime
     */
    public ShoppingCartProtocolInvoice(long xsollaPaymentId, BigDecimal amount, String v1, String currency, Date datetime) {
        this.xsollaPaymentId = xsollaPaymentId;
        this.amount = amount.setScale(2, BigDecimal.ROUND_FLOOR);
        this.v1 = v1;
        this.currency = currency;
        this.datetime = datetime;
        this.dryRun = false;
    }

    public long getXsollaPaymentId() {
        return xsollaPaymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getV1() {
        return v1;
    }

    public String getCurrency() {
        return currency;
    }

    public Date getDatetime() {
        return datetime;
    }

    public String getV2() {
        return v2;
    }

    public ShoppingCartProtocolInvoice setV2(String v2) {
        this.v2 = v2;
        return this;
    }

    public String getV3() {
        return v3;
    }

    public ShoppingCartProtocolInvoice setV3(String v3) {
        this.v3 = v3;
        return this;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public ShoppingCartProtocolInvoice setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
        return this;
    }

    public BigDecimal getUserAmount() {
        return userAmount;
    }

    public ShoppingCartProtocolInvoice setUserAmount(BigDecimal userAmount) {
        if (userAmount != null) {
            this.userAmount = userAmount.setScale(2, BigDecimal.ROUND_FLOOR);
        } else {
            this.userAmount = null;
        }
        return this;
    }

    public String getUserCurrency() {
        return userCurrency;
    }

    public ShoppingCartProtocolInvoice setUserCurrency(String userCurrency) {
        this.userCurrency = userCurrency;
        return this;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public ShoppingCartProtocolInvoice setTransferAmount(BigDecimal transferAmount) {
        if (transferAmount != null) {
            this.transferAmount = transferAmount.setScale(2, BigDecimal.ROUND_FLOOR);
        } else {
            this.transferAmount = null;
        }
        return this;
    }

    public String getTransferCurrency() {
        return transferCurrency;
    }

    public ShoppingCartProtocolInvoice setTransferCurrency(String transferCurrency) {
        this.transferCurrency = transferCurrency;
        return this;
    }

    public Integer getPid() {
        return pid;
    }

    public ShoppingCartProtocolInvoice setPid(Integer pid) {
        this.pid = pid;
        return this;
    }

    public Integer getGeotype() {
        return geotype;
    }

    public ShoppingCartProtocolInvoice setGeotype(Integer geotype) {
        this.geotype = geotype;
        return this;
    }

    public Map<String, List<String>> compareTo(ShoppingCartProtocolInvoice protocolInvoice) {
        this.difference = new HashMap<>();
        this.compareParameter("xsollaPaymentId", this.xsollaPaymentId, protocolInvoice.getXsollaPaymentId());
        this.compareParameter("amount", this.amount, protocolInvoice.getAmount());
        this.compareParameter("v1", this.v1, protocolInvoice.getV1());
        this.compareParameter("v2", this.v2, protocolInvoice.getV2());
        this.compareParameter("v3", this.v3, protocolInvoice.getV3());
        this.compareParameter("currency", this.currency, protocolInvoice.getCurrency());
        this.compareParameter("dryRun", this.dryRun, protocolInvoice.isDryRun());
        this.compareParameter("userAmount", this.userAmount, protocolInvoice.getUserAmount());
        this.compareParameter("userCurrency", this.userCurrency, protocolInvoice.getUserCurrency());
        this.compareParameter("transferAmount", this.transferCurrency, protocolInvoice.getTransferCurrency());
        this.compareParameter("pid", this.pid, protocolInvoice.getPid());
        this.compareParameter("geotype", this.geotype, protocolInvoice.getGeotype());
        return this.difference;
    }

    private void compareParameter(String name, Object thisValue, Object otherValue) {
        if (thisValue == null ? otherValue != null : !thisValue.equals(otherValue)) {
            this.difference.put(name, Arrays.asList(String.valueOf(thisValue), String.valueOf(otherValue)));
        }
    }

    private void compareParameter(String name, BigDecimal thisValue, BigDecimal otherValue) {
        if (thisValue == null ? otherValue != null : thisValue.compareTo(otherValue) != 0) {
            this.difference.put(name, Arrays.asList(String.valueOf(thisValue), String.valueOf(otherValue)));
        }
    }
}
