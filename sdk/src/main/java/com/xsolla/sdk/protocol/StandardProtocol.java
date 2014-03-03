package com.xsolla.sdk.protocol;


import com.xsolla.sdk.Project;
import com.xsolla.sdk.protocol.command.factory.StandardCommandFactory;
import com.xsolla.sdk.protocol.command.response.CommandResponse;
import com.xsolla.sdk.protocol.command.response.StandardCommandResponse;
import com.xsolla.sdk.protocol.storage.IPaymentStandardStorage;
import com.xsolla.sdk.protocol.storage.IUserStorage;
import com.xsolla.sdk.protocol.request.Request;
import com.xsolla.sdk.validator.IpChecker;

import java.util.Arrays;
import java.util.List;

public class StandardProtocol extends Protocol {

    protected StandardCommandFactory commandFactory;

    protected IUserStorage userStorage;
    protected IPaymentStandardStorage paymentStorage;

    public StandardProtocol(
            Project project,
            StandardCommandFactory commandFactory,
            IUserStorage userStorage,
            IPaymentStandardStorage paymentStorage,
            IpChecker ipChecker
    ) {
        super(project, ipChecker);
        this.commandFactory = commandFactory;
        this.userStorage = userStorage;
        this.paymentStorage = paymentStorage;
    }

    public IUserStorage getUserStorage() {
        return userStorage;
    }

    public IPaymentStandardStorage getPaymentStorage() {
        return paymentStorage;
    }

    protected CommandResponse doCommand(Request request) throws Exception {
        this.currentCommand = this.commandFactory.getCommand(this, request.getParam("command" ));
        return this.currentCommand.getResponse(request);
    }

    public CommandResponse getProtocolEmptyCommandResponse() {
        return new StandardCommandResponse();
    }

    public List<String> getProtocolCommands() {
        return Arrays.asList("check", "pay", "cancel");
    }
}
