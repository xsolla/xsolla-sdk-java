package com.xsolla.sdk.protocol.command;


import com.xsolla.sdk.User;
import com.xsolla.sdk.protocol.StandardProtocol;
import com.xsolla.sdk.protocol.command.response.CommandResponse;
import com.xsolla.sdk.protocol.command.response.PayStandardCommandResponse;
import com.xsolla.sdk.protocol.command.response.StandardCommandResponse;
import com.xsolla.sdk.protocol.invoice.StandardProtocolInvoice;
import com.xsolla.sdk.protocol.storage.IPaymentStandardStorage;
import com.xsolla.sdk.protocol.storage.IUserStorage;
import com.xsolla.sdk.protocol.request.Request;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PayStandard extends StandardCommand {

    public PayStandard(StandardProtocol protocol) {
        this.project = protocol.getProject();
        this.paymentStorage = protocol.getPaymentStorage();
        this.userStorage = protocol.getUserStorage();
    }

    public List<String> getRequiredParams() {
        return Arrays.asList("command", "md5", "id", "sum", "v1", "date");
    }

    protected CommandResponse process(Request request) throws Exception {
        User user = this.createUser(request);
        boolean hasUser = this.userStorage.isUserExists(user);
        if (!hasUser) {
            return new StandardCommandResponse() {{
                setResult(CODE_INVALID_ORDER_DETAILS);
                setComment("User not found");
            }};
        }
        Date date = this.getDateXsolla("Y-M-d H:m:s", request.getParam("date"));
        StandardProtocolInvoice protocolInvoice = new StandardProtocolInvoice(
                Long.parseLong(request.getParam("id")),
                new BigDecimal(request.getParam("sum")),
                user,
                date
        );
        protocolInvoice.setDryRun(this.isDryRun(request));
        final String id = this.paymentStorage.pay(protocolInvoice);
        return new PayStandardCommandResponse() {{
            setResult(CODE_SUCCESS);
            setIdShop(id);
        }};

    }

    public boolean checkSign(Request request) throws NoSuchAlgorithmException {
        return this.generateSign(request, Arrays.asList("command", "v1", "id")).equals(request.getParam("md5"));
    }

}
