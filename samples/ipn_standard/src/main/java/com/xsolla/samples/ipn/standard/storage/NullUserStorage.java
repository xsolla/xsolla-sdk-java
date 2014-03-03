package com.xsolla.samples.ipn.standard.storage;

import com.xsolla.sdk.User;
import com.xsolla.sdk.protocol.storage.IUserStorage;
import java.util.HashMap;

public class NullUserStorage implements IUserStorage {

    public boolean isUserExists(User user) {
        return true;
    }

    public HashMap<String, String> getAdditionalUserFields(User user) {
        return new HashMap<String, String>();
    }
}
