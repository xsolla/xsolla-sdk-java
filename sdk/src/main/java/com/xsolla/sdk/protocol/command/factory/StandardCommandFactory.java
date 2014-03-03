package com.xsolla.sdk.protocol.command.factory;

import com.xsolla.sdk.exception.WrongCommandException;
import com.xsolla.sdk.protocol.StandardProtocol;
import com.xsolla.sdk.protocol.command.Cancel;
import com.xsolla.sdk.protocol.command.Check;
import com.xsolla.sdk.protocol.command.Command;
import com.xsolla.sdk.protocol.command.PayStandard;
import org.apache.commons.lang3.StringUtils;

public class StandardCommandFactory {

    public Command getCommand(StandardProtocol protocol, String commandName) throws WrongCommandException {
        switch (commandName) {
            case "check":
                return new Check(protocol);
            case "pay":
                return new PayStandard(protocol);
            case "cancel":
                return new Cancel(protocol);
            default:
                String exceptionMessage = String.format(
                        "Wrong command '%s'. Available commands for Standard protocol are: '%s'.",
                        commandName,
                        StringUtils.join(protocol.getProtocolCommands(), "', '")
                );
                throw new WrongCommandException(exceptionMessage);
        }

    }
}
