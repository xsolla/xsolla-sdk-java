package com.xsolla.sdk.protocol.command;

import com.xsolla.sdk.exception.InvoiceNotFoundException;
import com.xsolla.sdk.protocol.Protocol;
import com.xsolla.sdk.protocol.command.response.CancelCommandResponse;
import com.xsolla.sdk.protocol.command.response.CommandResponse;
import com.xsolla.sdk.protocol.storage.IPaymentStorage;
import com.xsolla.sdk.protocol.request.Request;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class Cancel extends StandardCommand {

    protected IPaymentStorage paymentStorage;

    public Cancel(Protocol protocol) {
        this.project = protocol.getProject();
        this.paymentStorage = protocol.getPaymentStorage();
    }

    protected CommandResponse process(Request request) throws Exception {
        try {
            this.paymentStorage.cancel(Long.parseLong(request.getParam("id")));
            return new CancelCommandResponse() {{
               setResult(CODE_SUCCESS);
            }};
        } catch (InvoiceNotFoundException e) {
            final String commentNotFound = e.getMessage();
            return new CancelCommandResponse() {{
                setResult(CODE_INVALID_ORDER_DETAILS);
                setComment(commentNotFound);
            }};
        }
    }

    public boolean checkSign(Request request) throws NoSuchAlgorithmException {
        return this.generateSign(request, Arrays.asList("command", "id")).equals(request.getParam("md5"));
    }

    public List<String> getRequiredParams() {
        return Arrays.asList("command", "md5", "id");
    }
}
