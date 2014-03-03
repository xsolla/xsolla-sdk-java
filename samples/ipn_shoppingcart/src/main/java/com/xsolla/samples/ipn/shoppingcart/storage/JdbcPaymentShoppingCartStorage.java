package com.xsolla.samples.ipn.shoppingcart.storage;

import com.xsolla.sdk.exception.InvoiceNotFoundException;
import com.xsolla.sdk.exception.UnprocessableRequestException;
import com.xsolla.sdk.protocol.invoice.ShoppingCartProtocolInvoice;
import com.xsolla.sdk.protocol.storage.IPaymentShoppingCartStorage;
import org.apache.commons.lang3.StringUtils;
import java.sql.*;
import java.util.*;
import java.util.Date;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcPaymentShoppingCartStorage implements IPaymentShoppingCartStorage {

    protected Connection connection;

    public JdbcPaymentShoppingCartStorage(Connection connection) {
        this.connection = connection;
    }

    public void cancel(long invoiceId) throws Exception {
        if (!this.updateInvoice(invoiceId)) {
            PreparedStatement select = this.connection.prepareStatement(
                    "SELECT is_canceled, timestamp_canceled\n" +
                            "FROM xsolla_shopping_cart_invoice\n" +
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

    public String pay(ShoppingCartProtocolInvoice protocolInvoice) throws SQLException {
        if (this.updateInvoice(protocolInvoice)) {
            return protocolInvoice.getV1();
        }
        ShoppingCartProtocolInvoice existentInvoice = this.selectInvoice(protocolInvoice.getV1());
        ArrayList<String> paremetersDiff = this.getRepeatedNotificationDiff(existentInvoice, protocolInvoice);
        if (!paremetersDiff.isEmpty()) {
            throw new UnprocessableRequestException(String.format(
                    "Repeated payment notification is received for invoice v1=%s. " +
                    "But new payment notification parameters are not equal with previous: %s.",
                    protocolInvoice.getV1(),
                    StringUtils.join(paremetersDiff, ", ")
            ));
        }
        return protocolInvoice.getV1();
    }

    protected boolean updateInvoice(long invoiceId) throws SQLException {
        PreparedStatement update = this.connection.prepareStatement(
                "UPDATE xsolla_shopping_cart_invoice SET\n" +
                        "   is_canceled = 1,\n" +
                        "   timestamp_canceled = NOW()\n" +
                        "WHERE id_xsolla = ?\n" +
                        "   AND is_canceled = 0;"
        );
        update.setLong(1, invoiceId);
        update.execute();
        return update.getUpdateCount() > 0;
    }

    protected boolean updateInvoice(ShoppingCartProtocolInvoice protocolInvoice) throws SQLException {
        PreparedStatement update = this.connection.prepareStatement(
                "UPDATE xsolla_shopping_cart_invoice SET\n" +
                        "   id_xsolla = ?,\n" +
                        "   timestamp_ipn = NOW(),\n" +
                        "   timestamp_xsolla_ipn = ?,\n" +
                        "   user_amount = ?,\n" +
                        "   user_currency = ?,\n" +
                        "   transfer_amount = ?,\n" +
                        "   transfer_currency = ?,\n" +
                        "   pid = ?,\n" +
                        "   geotype = ?\n" +
                        "WHERE v1 = ?\n" +
                        "   AND v2 <=> ?\n" +
                        "   AND v3 <=> ?\n" +
                        "   AND invoice_amount = ?\n" +
                        "   AND invoice_currency = ?\n" +
                        "   AND is_dry_run = ?\n" +
                        "   AND id_xsolla IS NULL",
                RETURN_GENERATED_KEYS
        );
        update.setLong(1, protocolInvoice.getXsollaPaymentId());
        Date datetime = protocolInvoice.getDatetime();
        update.setDate(2, new java.sql.Date(datetime.getTime()));
        update.setBigDecimal(3, protocolInvoice.getUserAmount());
        update.setString(4,  protocolInvoice.getUserCurrency());
        update.setBigDecimal(5, protocolInvoice.getTransferAmount());
        update.setString(6, protocolInvoice.getTransferCurrency());
        this.nullSafeSet(update, 7, protocolInvoice.getPid());
        this.nullSafeSet(update, 8, protocolInvoice.getGeotype());
        update.setString(9, protocolInvoice.getV1());
        update.setString(10, protocolInvoice.getV2());
        update.setString(11, protocolInvoice.getV3());
        update.setBigDecimal(12, protocolInvoice.getAmount());
        update.setString(13, protocolInvoice.getCurrency());
        update.setBoolean(14, protocolInvoice.isDryRun());
        update.execute();
        return update.getUpdateCount() > 0;
    }

    protected ShoppingCartProtocolInvoice selectInvoice(String v1) throws SQLException {
        PreparedStatement select = this.connection.prepareStatement(
                "SELECT v1, v2, v3,\n" +
                        "   id_xsolla,\n" +
                        "   invoice_amount,\n" +
                        "   invoice_currency,\n" +
                        "   timestamp_xsolla_ipn,\n" +
                        "   user_amount,\n" +
                        "   user_currency,\n" +
                        "   transfer_amount,\n" +
                        "   transfer_currency,\n" +
                        "   pid, geotype,\n" +
                        "   is_dry_run\n" +
                        "FROM xsolla_shopping_cart_invoice\n" +
                        "WHERE v1 = ?;"
        );
        select.setString(1, v1);
        ResultSet resultSet = select.executeQuery();
        if (!resultSet.next()) {
            throw new UnprocessableRequestException(String.format(
                    "Invoice with v1=%s not found",
                    v1));
        }
        return this.createShoppingCartProtocolInvoice(resultSet);
    }

    protected ArrayList<String> getRepeatedNotificationDiff(
            ShoppingCartProtocolInvoice existentInvoice,
            ShoppingCartProtocolInvoice notificationInvoice
    ) {
        ArrayList<String> descriptions = new ArrayList<String>();
        Map<String, List<String>> invoicesDiff = existentInvoice.compareTo(notificationInvoice);
        for (String parameter : invoicesDiff.keySet()) {
            List<String> values = invoicesDiff.get(parameter);
            descriptions.add(String.format(
                    " %s(previous=%s,repeated=%s)",
                    parameter,
                    this.valueToString(values.get(0)),
                    this.valueToString(values.get(1))
            ));
        }
        return descriptions;
    }

    protected ShoppingCartProtocolInvoice createShoppingCartProtocolInvoice(ResultSet resultSet) throws SQLException {
        ShoppingCartProtocolInvoice protocolInvoice = new ShoppingCartProtocolInvoice(
                resultSet.getLong("id_xsolla"),
                resultSet.getBigDecimal("invoice_amount"),
                resultSet.getString("v1"),
                resultSet.getString("invoice_currency"),
                resultSet.getDate("timestamp_xsolla_ipn")
        );
        protocolInvoice.setV2(resultSet.getString("v2"))
                .setV3(resultSet.getString("v3"))
                .setDryRun(resultSet.getBoolean("is_dry_run"))
                .setUserAmount(resultSet.getBigDecimal("user_amount"))
                .setUserCurrency(resultSet.getString("user_currency"))
                .setTransferAmount(resultSet.getBigDecimal("transfer_amount"))
                .setTransferCurrency(resultSet.getString("transfer_currency"))
                .setPid(resultSet.getInt("pid"))
                .setGeotype(resultSet.getInt("geotype"));
        return protocolInvoice;
    }

    protected String valueToString(Object value) {
        return value == null ? "[null]" : value.toString();
    }

    protected void nullSafeSet(PreparedStatement preparedStatement, int position, Integer value) throws SQLException {
        if (value != null) {
            preparedStatement.setInt(position, value);
        } else {
            preparedStatement.setNull(position, Types.INTEGER);
        }
    }

}
