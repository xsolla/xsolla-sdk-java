package com.xsolla.samples.ipn.standard.storage;

import com.xsolla.sdk.exception.InvoiceNotFoundException;
import com.xsolla.sdk.exception.UnprocessableRequestException;
import com.xsolla.sdk.protocol.invoice.StandardProtocolInvoice;
import com.xsolla.sdk.protocol.storage.IPaymentStandardStorage;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import static java.sql.Statement.*;

public class JdbcPaymentStandardStorage implements IPaymentStandardStorage {

    protected Connection connection;

    public JdbcPaymentStandardStorage(Connection connection) {
        this.connection = connection;
    }

    public void cancel(long invoiceId) throws Exception {
        if (!this.updateInvoice(invoiceId)) {
            PreparedStatement select = this.connection.prepareStatement(
                    "SELECT is_canceled, timestamp_canceled\n" +
                            "FROM xsolla_standard_invoice\n" +
                            "WHERE id_xsolla = ?;"
            );
            select.setLong(1, invoiceId);
            ResultSet resultSet = select.executeQuery();
            if (!resultSet.next()) {
                throw new InvoiceNotFoundException("Invoice not found");
            }
            if (resultSet.getInt("is_canceled") != 1) {
                throw new Exception("Temporary error");
            }
        }
    }

    public String pay(StandardProtocolInvoice protocolInvoice) throws Exception {
        long id = insertInvoice(protocolInvoice);
        if (id > 0) {
            return String.valueOf(id);
        }
        return String.valueOf(this.selectInvoice(protocolInvoice));
    }

    protected boolean updateInvoice(long invoiceId) throws SQLException {
        PreparedStatement update = this.connection.prepareStatement(
                "UPDATE xsolla_standard_invoice SET\n" +
                        "is_canceled = 1,\n" +
                        "timestamp_canceled = NOW()\n" +
                        "WHERE id_xsolla = ?\n" +
                        "AND is_canceled = 0;"
        );
        update.setLong(1, invoiceId);
        update.execute();
        return update.getUpdateCount() > 0;
    }

    protected long insertInvoice(StandardProtocolInvoice protocolInvoice) throws SQLException {
        PreparedStatement insert = this.connection.prepareStatement(
                "INSERT IGNORE INTO xsolla_standard_invoice" +
                        "(v1, id_xsolla, amount_virtual_currency, timestamp_xsolla_ipn, is_dry_run)\n" +
                        "VALUES (?, ?, ?, ?, ?);",
                RETURN_GENERATED_KEYS
        );
        insert.setString(1, protocolInvoice.getUser().getV1());
        insert.setLong(2, protocolInvoice.getXsollaPaymentId());
        insert.setBigDecimal(3, protocolInvoice.getVirtualCurrencyAmount());
        Date date = protocolInvoice.getDate();
        insert.setDate(4, new java.sql.Date(date.getTime()));
        insert.setBoolean(5, protocolInvoice.isDryRun());
        insert.execute();
        ResultSet generatedKeys = insert.getGeneratedKeys();
        if (generatedKeys != null && generatedKeys.next()) {
            return generatedKeys.getLong(1);
        }
        return 0L;
    }

    protected long selectInvoice(StandardProtocolInvoice protocolInvoice) throws Exception {
        PreparedStatement select = connection.prepareStatement(
                "SELECT id, v1, amount_virtual_currency, timestamp_xsolla_ipn, is_dry_run\n" +
                        "FROM xsolla_standard_invoice\n" +
                        "WHERE id_xsolla = ?;"
        );
        select.setLong(1, protocolInvoice.getXsollaPaymentId());
        ResultSet resultSet = select.executeQuery();
        if (!resultSet.next()) {
            throw new Exception("Temporary error");
        }
        ArrayList<String> diffMessages = new ArrayList<String>();
        String diffFormat = " %s(previous=%s,repeated=%s)";
        String v1 = protocolInvoice.getUser().getV1();
        if (!resultSet.getString("v1").equals(v1)) {
            diffMessages.add(String.format(diffFormat, "v1", resultSet.getString("v1"), v1));
        }
        if (!resultSet.getString("amount_virtual_currency").equals(protocolInvoice.getVirtualCurrencyAmount().toString())) {
            diffMessages.add(String.format(
                    diffFormat,
                    "virtualCurrencyAmount",
                    resultSet.getString("amount_virtual_currency"),
                    protocolInvoice.getVirtualCurrencyAmount().toString()
            ));
        }
        if (!diffMessages.isEmpty()) {
            throw new UnprocessableRequestException(String.format(
                    "Repeated payment notification is received for invoice with id_xsolla=%d. " +
                    "But new payment notification parameters are not equal with previous: %s",
                    protocolInvoice.getXsollaPaymentId(),
                    StringUtils.join(diffMessages, ", ")
            ));
        }
        return resultSet.getLong("id");
    }
}
