package com.xsolla.sdk.protocol.storage;

import com.xsolla.sdk.protocol.invoice.ShoppingCartProtocolInvoice;

public interface IPaymentShoppingCartStorage extends IPaymentStorage {

    /**
     * Pay IPN
     * Notification parameters packed into protocolInvoice.
     * If this xsollaPaymentId already exists and v1, v2, v3, amount, currency dryRun are the same,
     * you MUST return your existent payment ID.
     *
     * @param protocolInvoice Container ShoppingCart parameters
     * @return unique payment ID in your system
     * @throws com.xsolla.sdk.exception.UnprocessableRequestException Fatal error. Not notify this payment.
     * @throws Exception Technical error.
     */
    public String pay(ShoppingCartProtocolInvoice protocolInvoice) throws Exception;
}
