package com.xsolla.sdk.protocol.command;

import com.xsolla.sdk.protocol.ShoppingCartProtocol;
import com.xsolla.sdk.protocol.command.response.CommandResponse;
import com.xsolla.sdk.protocol.command.response.PayShoppingCartCommandResponse;
import com.xsolla.sdk.protocol.invoice.ShoppingCartProtocolInvoice;
import com.xsolla.sdk.protocol.storage.IPaymentShoppingCartStorage;
import com.xsolla.sdk.protocol.request.Request;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class PayShoppingCart extends Command {

    protected IPaymentShoppingCartStorage paymentStorage;

    public PayShoppingCart(ShoppingCartProtocol protocol) {
        this.project = protocol.getProject();
        this.paymentStorage = protocol.getPaymentStorage();
    }

    public List<String> getRequiredParams() {
        return Arrays.asList("command", "sign", "id", "v1", "amount", "currency", "datetime");
    }

    protected CommandResponse process(Request request) throws Exception {
        Date datetime = this.getDateXsolla("YMdHms", request.getParam("datetime"));
        ShoppingCartProtocolInvoice protocolInvoice = new ShoppingCartProtocolInvoice(
                Long.parseLong(request.getParam("id")),
                new BigDecimal(request.getParam("amount")),
                request.getParam("v1"),
                request.getParam("currency"),
                datetime
        );
        String pid = request.getParam("pid");
        String geotype = request.getParam("geotype");
        protocolInvoice.setV2(this.emptyStringToNull(request.getParam("v2")))
                .setV3(this.emptyStringToNull(request.getParam("v3")))
                .setDryRun(this.isDryRun(request))
                .setPid(pid == null ? null : Integer.parseInt(pid))
                .setGeotype(geotype == null ? null : Integer.parseInt(geotype));
        if (request.hasParam("userAmount")) {
            protocolInvoice.setUserAmount(new BigDecimal(request.getParam("userAmount")));
        }
        protocolInvoice.setUserCurrency(request.getParam("userCurrency"));
        if (request.hasParam("transferAmount")) {
            protocolInvoice.setTransferAmount(new BigDecimal(request.getParam("transferAmount")));
        }
        protocolInvoice.setTransferCurrency(request.getParam("transferCurrency"));
        final String idShop = this.paymentStorage.pay(protocolInvoice);
        return new PayShoppingCartCommandResponse() {{
            setResult(CODE_SUCCESS);
            setIdShop(idShop);
        }};
    }

    public boolean checkSign(Request request) throws NoSuchAlgorithmException {
        return this.generateSign(request, Arrays.asList("v1", "amount", "currency", "id")).equals(request.getParam("sign"));
    }

    public CommandResponse getEmptyResponse() {
        return new PayShoppingCartCommandResponse();
    }
}
