package com.xsolla.sdk.protocol.command;

import com.xsolla.sdk.User;
import com.xsolla.sdk.protocol.command.response.CommandResponse;
import com.xsolla.sdk.protocol.command.response.StandardCommandResponse;
import com.xsolla.sdk.protocol.storage.IPaymentStandardStorage;
import com.xsolla.sdk.protocol.storage.IUserStorage;
import com.xsolla.sdk.protocol.request.Request;

abstract public class StandardCommand extends Command {

    protected IUserStorage userStorage;
    protected IPaymentStandardStorage paymentStorage;

    protected User createUser(Request request) {
        User user = new User(request.getParam("v1"));
        if (request.hasParam("v2")) {
            user.setV2(request.getParam("v2"));
        }
        if (request.hasParam("v3")) {
            user.setV3(request.getParam("v3"));
        }
        return user;
    }

    public CommandResponse getEmptyResponse() {
        return new StandardCommandResponse();
    }

}
