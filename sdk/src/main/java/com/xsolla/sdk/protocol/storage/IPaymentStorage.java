package com.xsolla.sdk.protocol.storage;

import com.xsolla.sdk.exception.InvoiceNotFoundException;
import com.xsolla.sdk.exception.UnprocessableRequestException;

public interface IPaymentStorage {

    /**
     * Rollback payment. If payment already canceled you MUST NOT throw any exceptions.
     * @param invoiceId Payment ID to rollback.
     * @throws com.xsolla.sdk.exception.InvoiceNotFoundException If payment with given ID not exists
     * @throws com.xsolla.sdk.exception.UnprocessableRequestException If payment can not be canceled.
     * @throws Exception Technical error.
     */
    public void cancel(long invoiceId) throws Exception;
}
