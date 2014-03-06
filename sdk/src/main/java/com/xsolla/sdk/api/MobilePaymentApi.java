package com.xsolla.sdk.api;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.xsolla.sdk.Invoice;
import com.xsolla.sdk.Project;
import com.xsolla.sdk.User;
import com.xsolla.sdk.exception.*;
import com.xsolla.sdk.exception.SecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

/**
 * @link http://xsolla.github.io/en/APImobile.html
 */
public class MobilePaymentApi {

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_ERROR_WRONG_SIGN = 3;
    public static final int CODE_ERROR_INTERNAL_SERVER = 1;

    public static final String API_URL = "/mobile/payment/index.php";

    protected Project project;
    protected String url;
    protected AsyncHttpClient asyncHttpClient;

    public MobilePaymentApi(AsyncHttpClient asyncHttpClient, String baseUrl, Project project)
    {
        this.asyncHttpClient = asyncHttpClient;
        this.url = baseUrl + API_URL;
        this.project = project;
    }

    /**
     * Issue an invoice to the user.
     * @param user User
     * @param invoice Invoice, that contains amount of game currency and rubles.
     * @return Invoice with ID.
     * @throws Exception
     */
    public Invoice createInvoice(final User user, final Invoice invoice) throws Exception {
        final String email = user.getEmail();
        final int projectId = this.project.getProjectId();
        LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>() {{
            put("command", "invoice");
            put("project", String.valueOf(projectId));
            put("v1", nullToEmptyString(user.getV1()));
            put("v2", nullToEmptyString(user.getV2()));
            put("v3", nullToEmptyString(user.getV3()));
            put("sum", nullToEmptyString(invoice.getAmount()));
            put("out", nullToEmptyString(invoice.getVirtualCurrencyAmount()));
            put("phone", nullToEmptyString(user.getPhone()));
            put("userip", nullToEmptyString(user.getUserIP()));
        }};
        if (email != null && !email.isEmpty()) {
            parameters.put("email", email);
        }
        Document resultDocument = this.send(parameters);
        this.checkCodeResult(resultDocument);
        Invoice resultInvoice = new Invoice();
        String resultInvoiceValue = this.getFirstNodeValueByTag(resultDocument, "invoice");
        return resultInvoice.setId(Long.parseLong(resultInvoiceValue));
    }

    /**
     * Determine the amount of game currency, which the user will get paying a certain sum in Russian rubles.
     * @param user User
     * @param amount BigDecimal amount in rubles.
     * @return Invoice, that contains amount of game currency and rubles.
     * @throws Exception
     */
    public Invoice calculateVirtualCurrencyAmount(User user, BigDecimal amount) throws Exception {
        return this.calculate(user, "sum", amount.toString());
    }

    /**
     * Calculate the sum in rubles, which the user has to pay to get a certain amount of game currency.
     * @param user User
     * @param virtualCurrencyAmount BigDecimal amount in game currency.
     * @return Invoice, that contains amount of game currency and rubles.
     * @throws Exception
     */
    public Invoice calculateAmount(User user, BigDecimal virtualCurrencyAmount) throws Exception {
        return this.calculate(user, "out", virtualCurrencyAmount.toString());
    }

    protected Invoice calculate(final User user, String operationalParameterName, String operationalParameterValue) throws Exception {
        final int projectId = this.project.getProjectId();
        LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>() {{
            put("command", "calculate");
            put("project", String.valueOf(projectId));
            put("phone", nullToEmptyString(user.getPhone()));
        }};
        parameters.put(operationalParameterName, operationalParameterValue);
        Document result = this.send(parameters);
        this.checkCodeResult(result);
        BigDecimal amount = new BigDecimal(this.getFirstNodeValueByTag(result, "sum"));
        BigDecimal virtualCurrencyAmount = new BigDecimal(this.getFirstNodeValueByTag(result, "out"));
        return new Invoice().setAmount(amount).setVirtualCurrencyAmount(virtualCurrencyAmount);
    }

    protected String nullToEmptyString(Object value) {
        return value == null ? "" : value.toString();
    }

    protected String getFirstNodeValueByTag(Document document, String tagName) throws InvalidResponseException {
        NodeList nodeList = document.getElementsByTagName(tagName);
        if (nodeList.getLength() == 0) {
            throw new InvalidResponseException(String.format("Response not contains element with tag '%s'", tagName));
        }
        Node node = nodeList.item(0);
        return node.getFirstChild().getNodeValue();
    }

    protected StringBuilder createSignStringBuilder(LinkedHashMap<String, String> parameters) {
        StringBuilder signStringBuilder = new StringBuilder();
        for (String key : parameters.keySet()) {
            signStringBuilder.append(parameters.get(key));
        }
        return signStringBuilder;
    }

    protected Document send(LinkedHashMap<String, String> parameters)
            throws NoSuchAlgorithmException, IOException, ExecutionException, InterruptedException, ParserConfigurationException, SAXException {
        StringBuilder signStringBuilder = this.createSignStringBuilder(parameters)
                .append(this.project.getSecretKey());
        byte[] md5Bytes = MessageDigest.getInstance("MD5")
                .digest(signStringBuilder.toString().getBytes());
        String md5String = String.format("%032x",new BigInteger(1, md5Bytes));
        parameters.put("md5", md5String);
        AsyncHttpClient.BoundRequestBuilder requestBuilder = this.asyncHttpClient.prepareGet(this.url);
        this.setQueryParameters(requestBuilder, parameters);
        Response response = requestBuilder.execute().get();
        String responseBody = response.getResponseBody();
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return documentBuilder.parse(new ByteArrayInputStream(responseBody.getBytes("UTF-8")));
    }

    protected void setQueryParameters(
            AsyncHttpClient.BoundRequestBuilder requestBuilder,
            LinkedHashMap<String, String> parameters
    ) {
        for (String key : parameters.keySet()) {
            requestBuilder.addQueryParameter(key, parameters.get(key));
        }
    }

    protected void checkCodeResult(Document result) throws InvalidResponseException {
        String codeString = this.getFirstNodeValueByTag(result, "result");
        int code = Integer.parseInt(codeString);
        String comment = this.getFirstNodeValueByTag(result, "comment");
        switch (code) {
            case CODE_SUCCESS:
                return;
            case CODE_ERROR_WRONG_SIGN:
                throw new SecurityException(comment);
            case CODE_ERROR_INTERNAL_SERVER:
                throw new InternalServerException(comment);
        }
        throw new InvalidArgumentException(comment);
    }

}
