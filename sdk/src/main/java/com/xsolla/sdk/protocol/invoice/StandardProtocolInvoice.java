package com.xsolla.sdk.protocol.invoice;

import com.xsolla.sdk.User;

import java.math.BigDecimal;
import java.util.*;

public class StandardProtocolInvoice {

    protected long xsollaPaymentId;
    protected BigDecimal virtualCurrencyAmount;
    protected User user;
    protected Date date;
    protected boolean dryRun;

    private Map<String, List<String>> difference;

    public StandardProtocolInvoice(long xsollaPaymentId, BigDecimal virtualCurrencyAmount, User user, Date date) {
        this.xsollaPaymentId = xsollaPaymentId;
        this.virtualCurrencyAmount = virtualCurrencyAmount.setScale(2, BigDecimal.ROUND_CEILING);
        this.user = user;
        this.date = date;
        this.dryRun = false;
    }

    public long getXsollaPaymentId() {
        return xsollaPaymentId;
    }

    public BigDecimal getVirtualCurrencyAmount() {
        return virtualCurrencyAmount;
    }

    public User getUser() {
        return user;
    }

    public Date getDate() {
        return date;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public StandardProtocolInvoice setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
        return this;
    }

    public Map<String, List<String>> compareTo(StandardProtocolInvoice protocolInvoice) {
        this.difference = new HashMap<>();
        this.compareParameter("xsollaPaymentId", this.xsollaPaymentId, protocolInvoice.getXsollaPaymentId());
        this.compareParameter("virtualCurrencyAmount", this.virtualCurrencyAmount, protocolInvoice.getVirtualCurrencyAmount());
        this.compareParameter("v1", this.user.getV1(), protocolInvoice.getUser().getV1());
        this.compareParameter("dryRun", this.dryRun, protocolInvoice.isDryRun());
        return this.difference;
    }

    private void compareParameter(String name, Object thisValue, Object otherValue) {
        if (!thisValue.equals(otherValue)) {
            this.difference.put(name, Arrays.asList(String.valueOf(thisValue), String.valueOf(otherValue)));
        }
    }

    private void compareParameter(String name, BigDecimal thisValue, BigDecimal otherValue) {
        if (thisValue != null && (thisValue.compareTo(otherValue) != 0)) {
            this.difference.put(name, Arrays.asList(String.valueOf(thisValue), String.valueOf(otherValue)));
        }
    }
}
