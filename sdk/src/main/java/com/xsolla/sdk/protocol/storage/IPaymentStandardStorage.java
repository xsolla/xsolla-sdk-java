package com.xsolla.sdk.protocol.storage;

import com.xsolla.sdk.User;
import com.xsolla.sdk.protocol.invoice.StandardProtocolInvoice;

import java.math.BigDecimal;
import java.util.Date;

public interface IPaymentStandardStorage extends IPaymentStorage {

    /**
     * Pay IPN
     * Notification parameters packed into protocolInvoice.
     * If this xsollaPaymentId already exists and v1, virtualCurrencyAmount, dryRun are the same,
     * you MUST return your existent payment ID
     *
     * @param protocolInvoice StandardProtocolInvoice parameters container
     * @return unique payment ID in your system
     * @throws com.xsolla.sdk.exception.UnprocessableRequestException Fatal error. Not notify this payment.
     * @throws Exception Technical error.
     */
    public String pay(StandardProtocolInvoice protocolInvoice) throws Exception;
}
