package com.xsolla.sdk.paymentpage;

import com.ning.http.client.AsyncHttpClient;
import com.xsolla.sdk.Invoice;
import com.xsolla.sdk.Project;
import com.xsolla.sdk.User;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.exec.util.MapUtils;
import org.apache.commons.lang3.StringUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;

public class UrlBuilder {

    public static final String BASE_URL = "https://secure.xsolla.com/paystation2/?";
    public static final String SANDBOX_URL = "https://sandbox-secure.xsolla.com/paystation2/?";

    protected Project project;

    protected Map<String, String> parameters;
    protected Map<String, String> immutableParameters;
    protected Map<String, String> defaultParameters;
    protected Set<String> hiddenParameters;
    protected Set<String> lockedParameters;

    protected final Set<String> immutableLockedParameters = new HashSet<String>() {{
        add("project");
        add("signparams");
    }};

    protected final Set<String> defaultLockedParameters = new HashSet<>(Arrays.asList(
            "theme", "project", "signparams", "v0", "v1", "v2", "v3", "out", "email", "currency", "userip",
            "allowSubscription", "fastcheckout", "id_package"
    ));

    public UrlBuilder(Project project, Map<String, String> immutableParameters) {
        this.init(project, immutableParameters);
    }

    public UrlBuilder(Project project) {
        this.init(project, new HashMap<String, String>());
    }

    private void init(Project project, Map<String, String> immutableParameters) {
        this.project = project;
        this.immutableParameters = immutableParameters;
        this.clear();
    }

    /**
     * Additional parameters are described in documentation http://xsolla.github.io/en/pswidget.html#title4
     * @param name
     * @param value
     * @param lockForUser Denies user to change parameter value on payment page. Also parameter will be hidden on payment page
     * @param hideFromUser Hides parameter value on payment page
     * @return
     */
    public UrlBuilder setParameter(String name, String value, boolean lockForUser, boolean hideFromUser) {
        if (value == null || value.isEmpty()) {
            return this;
        }
        this.parameters.put(name, value);
        if (hideFromUser) {
            this.hiddenParameters.add(name);
        }
        if (lockForUser) {
            this.lockParameterForUser(name);
        } else {
            this.unlockParameterForUser(name);
        }
        return this;
    }

    /**
     * Additional parameters are described in documentation http://xsolla.github.io/en/pswidget.html#title4
     * Parameter be hidden and locked, see setParameter(String name, String value, boolean lockForUser, boolean hideFromUser)
     * @param name
     * @param value
     * @return
     */
    public UrlBuilder setParameter(String name, String value) {
        return this.setParameter(name, value, false, false);
    }

    /**
     * Denies user change parameter value on payment page
     * @param name
     * @return
     */
    public UrlBuilder lockParameterForUser(String name) {
        this.lockedParameters.add(name);
        return this;
    }

    /**
     * Allows user to change parameter value on payment page
     * @param name
     * @return
     */
    public UrlBuilder unlockParameterForUser(String name) {
        if (this.lockedParameters.contains(name)) {
            this.lockedParameters.remove(name);
        }
        return this;
    }

    /**
     * Set parameters from User object
     * @param user
     * @param lockForUser
     * @param hideFromUser
     * @return
     */
    public UrlBuilder setUser(User user, boolean lockForUser, boolean hideFromUser) {
        this.setParameter("v1", user.getV1(), lockForUser, hideFromUser);
        this.setParameter("v2", user.getV2(), lockForUser, hideFromUser);
        this.setParameter("v3", user.getV3(), lockForUser, hideFromUser);
        this.setParameter("email", user.getEmail(), lockForUser, hideFromUser);
        this.setParameter("userip", user.getUserIP(), lockForUser, hideFromUser);
        this.setParameter("phone", user.getPhone(), lockForUser, hideFromUser);
        return this;
    }

    /**
     * Set parameters from User object
     * @param user
     * @return UrlBuilder
     */
    public UrlBuilder setUser(User user) {
        return setUser(user, true, false);
    }

    /**
     * Set parameters from Invoice object
     * @param invoice
     * @param lockForUser
     * @param hideFromUser
     * @return UrlBuilder
     */
    public UrlBuilder setInvoice(Invoice invoice, boolean lockForUser, boolean hideFromUser) {
        BigDecimal out = invoice.getVirtualCurrencyAmount();
        if (out != null) {
            this.setParameter("out", out.toString(), lockForUser, hideFromUser);
        }
        BigDecimal amount = invoice.getAmount();
        if (amount != null) {
            this.setParameter("amount", amount.toString(), lockForUser, hideFromUser);
        }
        this.setParameter("currency", invoice.getCurrency(), lockForUser, hideFromUser);
        return this;
    }

    /**
     * Set parameters from Invoice object
     * @param invoice
     * @return UrlBuilder
     */
    public UrlBuilder setInvoice(Invoice invoice) {
        return this.setInvoice(invoice, true, false);
    }

    /**
     * Set local parameter
     * @param locale 2-letter definition is used according to ISO 639-1 standard
     * @return UrlBuilder
     */
    public UrlBuilder setLocale(String locale) {
        return this.setParameter("local", locale);
    }

    /**
     * Set country parameter
     * @param country 2-letter definition of the country is used according to ISO 3166-1 alpha-2 standard
     * @return UrlBuilder
     */
    public UrlBuilder setCountry(String country) {
        return this.setParameter("country",  country);
    }

    public String getUrl(String baseUrl) throws NoSuchAlgorithmException {
        Map<String, String> parameters = MapUtils.merge(this.parameters, this.immutableParameters);
        parameters.put("project", String.valueOf(this.project.getProjectId()));
        if (!this.hiddenParameters.isEmpty()) {
            String implodedHiddenParameters = this.implodeUniqueParameters(new ArrayList<String>(this.hiddenParameters));
            parameters.put("hidden", implodedHiddenParameters);
        }
        String lockedParametersString = this.getLockedParametersString();
        if (!lockedParametersString.isEmpty()) {
            parameters.put("signparams", lockedParametersString);
        }
        parameters.put("sign", this.generateSign(parameters));
        return this.buildUrl(baseUrl, parameters);
    }

    public String getUrl() throws NoSuchAlgorithmException {
        return this.getUrl(BASE_URL);
    }

    protected String buildUrl(String baseUrl, Map<String, String> parameters) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder boundRequestBuilder = asyncHttpClient.prepareGet(baseUrl);
        for (String key : new TreeSet<>(parameters.keySet())) {
            boundRequestBuilder.addQueryParameter(key, parameters.get(key));
        }
        return boundRequestBuilder.build().getUrl();
    }

    protected String getLockedParametersString() {
        Set<String> lockedParameters = this.getSignParametersSortedKeys();
        if (SetUtils.isEqualSet(lockedParameters, this.defaultLockedParameters)) {
            return "";
        }
        return this.implodeUniqueParameters(new ArrayList<String>(this.defaultLockedParameters));
    }

    protected String implodeUniqueParameters(List<String> parameters) {
        return StringUtils.join(new HashSet<>(parameters), ',');
    }

    protected String generateSign(Map<String, String> parameters) throws NoSuchAlgorithmException {
        TreeSet<String> keys = this.getSignParametersSortedKeys();
        StringBuilder parametersForSign = new StringBuilder();
        for (String key : keys) {
            if (parameters.containsKey(key)) {
                parametersForSign.append(key).append('=').append(parameters.get(key));
            }
        }
        parametersForSign.append(this.project.getSecretKey());
        byte[] sign = MessageDigest.getInstance("MD5")
                .digest(parametersForSign.toString().getBytes());
        return String.format("%032x",new BigInteger(1, sign));
    }

    protected TreeSet<String> getSignParametersSortedKeys() {
        TreeSet<String> mergedParameters =  new TreeSet<>(this.lockedParameters);
        mergedParameters.addAll(this.immutableLockedParameters);
        return mergedParameters;
    }

    protected UrlBuilder clear() {
        this.parameters = new HashMap<>();
        this.defaultParameters = new HashMap<>();
        this.hiddenParameters = new HashSet<>();
        this.lockedParameters = this.defaultLockedParameters;
        return this;
    }
}
