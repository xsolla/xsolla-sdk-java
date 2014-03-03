package com.xsolla.sdk.protocol.command.factory;

import com.xsolla.sdk.exception.WrongCommandException;
import com.xsolla.sdk.protocol.ShoppingCartProtocol;
import com.xsolla.sdk.protocol.command.Cancel;
import com.xsolla.sdk.protocol.command.Command;
import com.xsolla.sdk.protocol.command.PayShoppingCart;
import org.apache.commons.lang3.StringUtils;

public class ShoppingCartCommandFactory {

    public Command getCommand(ShoppingCartProtocol protocol, String commandName) throws WrongCommandException {
        switch (commandName) {
            case "pay":
                return new PayShoppingCart(protocol);
            case "cancel":
                return new Cancel(protocol);
            default:
                String exceptionMessage = String.format(
                        "Wrong command '%s'. Available commands for protocol ShoppingCart are: '%s'.",
                        commandName,
                        StringUtils.join(protocol.getProtocolCommands(), "', '" )
                );
                throw new WrongCommandException(exceptionMessage);
        }
    }
}
