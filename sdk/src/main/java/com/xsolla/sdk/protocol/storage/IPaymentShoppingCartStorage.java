package com.xsolla.sdk.protocol.storage;

import com.xsolla.sdk.protocol.invoice.ShoppingCartProtocolInvoice;

public interface IPaymentShoppingCartStorage extends IPaymentStorage {

    public String pay(ShoppingCartProtocolInvoice protocolInvoice) throws Exception;
}
