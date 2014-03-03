package com.xsolla.sdk.protocol.command;

import com.xsolla.sdk.User;
import com.xsolla.sdk.protocol.StandardProtocol;
import com.xsolla.sdk.protocol.command.response.CheckCommandResponse;
import com.xsolla.sdk.protocol.command.response.CommandResponse;
import com.xsolla.sdk.protocol.request.Request;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Check extends StandardCommand {

    public Check(StandardProtocol protocol) {
        this.project = protocol.getProject();
        this.userStorage = protocol.getUserStorage();
    }

    protected CommandResponse process(Request request) throws Exception {
        CheckCommandResponse commandResponse = new CheckCommandResponse();
        User user = this.createUser(request);
        boolean hasUser = this.userStorage.isUserExists(user);
        if (hasUser) {
            commandResponse.setResult(CheckCommandResponse.CODE_SUCCESS);
            HashMap<String, String> spec = this.userStorage.getAdditionalUserFields(user);
            if (spec.size() > 0) {
                commandResponse.setSpecification(spec);
            }
        } else {
            commandResponse.setResult(CheckCommandResponse.CODE_USER_NOT_FOUND);
        }
        return commandResponse;
    }

    public boolean checkSign(Request request) throws NoSuchAlgorithmException {
        return this.generateSign(request, Arrays.asList("command", "v1")).equals(request.getParam("md5"));
    }

    public List<String> getRequiredParams() {
        return Arrays.asList("command", "v1", "md5");
    }
}
