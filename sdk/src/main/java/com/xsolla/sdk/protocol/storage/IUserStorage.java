package com.xsolla.sdk.protocol.storage;

import com.xsolla.sdk.User;

import java.sql.SQLException;
import java.util.HashMap;

public interface IUserStorage {

    public boolean isUserExists(User user) throws Exception;

    public HashMap<String, String> getAdditionalUserFields(User user) throws Exception;
}
