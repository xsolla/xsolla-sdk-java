package com.xsolla.sdk.protocol.storage;

import com.xsolla.sdk.User;
import com.xsolla.sdk.protocol.invoice.StandardProtocolInvoice;

import java.math.BigDecimal;
import java.util.Date;

public interface IPaymentStandardStorage extends IPaymentStorage {

    public String pay(StandardProtocolInvoice protocolInvoice) throws Exception;
}
