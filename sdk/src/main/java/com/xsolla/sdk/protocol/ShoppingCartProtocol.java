package com.xsolla.sdk.protocol;

import com.xsolla.sdk.Project;
import com.xsolla.sdk.protocol.command.factory.ShoppingCartCommandFactory;
import com.xsolla.sdk.protocol.command.response.CommandResponse;
import com.xsolla.sdk.protocol.command.response.ShoppingCartCommandResponse;
import com.xsolla.sdk.protocol.storage.IPaymentShoppingCartStorage;
import com.xsolla.sdk.protocol.request.Request;
import com.xsolla.sdk.validator.IpChecker;

import java.util.Arrays;
import java.util.List;

public class ShoppingCartProtocol extends Protocol {

    protected ShoppingCartCommandFactory commandFactory;

    protected IPaymentShoppingCartStorage paymentStorage;

    public ShoppingCartProtocol(
            Project project,
            ShoppingCartCommandFactory commandFactory,
            IPaymentShoppingCartStorage paymentStorage,
            IpChecker ipChecker
    ) {
        super(project, ipChecker);
        this.commandFactory = commandFactory;
        this.paymentStorage = paymentStorage;
    }

    public IPaymentShoppingCartStorage getPaymentStorage() {
        return paymentStorage;
    }

    protected CommandResponse doCommand(Request request) throws Exception {
        this.currentCommand = this.commandFactory.getCommand(this, request.getParam("command" ));
        return this.currentCommand.getResponse(request);
    }

    public CommandResponse getProtocolEmptyCommandResponse() {
        return new ShoppingCartCommandResponse();
    }

    public List<String> getProtocolCommands() {
        return Arrays.asList("pay", "cancel" );
    }
}
