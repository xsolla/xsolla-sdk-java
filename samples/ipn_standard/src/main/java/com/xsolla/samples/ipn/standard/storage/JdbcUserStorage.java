package com.xsolla.samples.ipn.standard.storage;

import com.xsolla.sdk.User;
import com.xsolla.sdk.protocol.storage.IUserStorage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class JdbcUserStorage implements IUserStorage {

    protected Connection connection;

    public JdbcUserStorage(Connection connection) {
        this.connection = connection;
    }

    public boolean isUserExists(User user) throws Exception {
        PreparedStatement select = this.connection.prepareStatement(
                "SELECT 1 FROM xsolla_standard_user WHERE v1 = ? AND v2 <=> ? AND v3 <=> ?;"
        );
        select.setString(1, user.getV1());
        select.setString(2, user.getV2());
        select.setString(3, user.getV3());
        ResultSet resultSet = select.executeQuery();
        return resultSet.next();
    }

    public HashMap<String, String> getAdditionalUserFields(User user) {
        return new HashMap<String, String>();
    }
}
