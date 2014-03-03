package com.xsolla.sdk.protocol.storage;

import com.xsolla.sdk.exception.InvoiceNotFoundException;
import com.xsolla.sdk.exception.UnprocessableRequestException;

public interface IPaymentStorage {

    public void cancel(long invoiceId) throws Exception;
}
