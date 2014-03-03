package com.xsolla.samples.ipn.standard.storage;

import com.xsolla.sdk.User;
import com.xsolla.sdk.exception.InvoiceNotFoundException;
import com.xsolla.sdk.exception.UnprocessableRequestException;
import com.xsolla.sdk.protocol.invoice.StandardProtocolInvoice;
import com.xsolla.sdk.protocol.storage.IPaymentStandardStorage;
import java.math.BigDecimal;
import java.util.Date;

public class NullPaymentStandardStorage implements IPaymentStandardStorage {

    public void cancel(long invoiceId) throws InvoiceNotFoundException, UnprocessableRequestException {}

    public String pay(StandardProtocolInvoice protocolInvoice) {
        return "A123";
    }
}
