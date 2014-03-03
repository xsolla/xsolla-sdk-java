package com.xsolla.sdk.protocol;

import com.xsolla.sdk.Project;
import com.xsolla.sdk.protocol.command.factory.ShoppingCartCommandFactory;
import com.xsolla.sdk.protocol.command.factory.StandardCommandFactory;
import com.xsolla.sdk.protocol.storage.IPaymentShoppingCartStorage;
import com.xsolla.sdk.protocol.storage.IPaymentStandardStorage;
import com.xsolla.sdk.protocol.storage.IUserStorage;
import com.xsolla.sdk.validator.IpChecker;

public class ProtocolFactory {

    protected Project project;
    protected IpChecker ipChecker;

    public ProtocolFactory(Project project, IpChecker ipChecker) {
        this.project = project;
        this.ipChecker = ipChecker;
    }

    public ProtocolFactory(Project project) {
        this.project = project;
        this.ipChecker = null;
    }

    public ShoppingCartProtocol getShoppingCartProtocol(IPaymentShoppingCartStorage paymentStorage) {
        return new ShoppingCartProtocol(
                this.project,
                new ShoppingCartCommandFactory(),
                paymentStorage,
                this.ipChecker
        );
    }

    public StandardProtocol getStandardProtocol(IUserStorage userStorage, IPaymentStandardStorage paymentStorage) {
        return new StandardProtocol(
                this.project,
                new StandardCommandFactory(),
                userStorage,
                paymentStorage,
                ipChecker
        );
    }
}
