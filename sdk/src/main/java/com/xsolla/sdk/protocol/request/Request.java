package com.xsolla.sdk.protocol.request;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Request {

    protected String clientIP;
    protected String host;
    protected String uri;
    protected LinkedHashMap<String, String> params;
    protected HashMap<String, List<String>> headers;

    public Request() {
        this.params = new LinkedHashMap<>();
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public LinkedHashMap<String, String> getParams() {
        return this.params;
    }

    public Request setParams(LinkedHashMap<String, String> params) {
        this.params = params;
        return this;
    }

    public String getParam(String param) {
        return this.params.get(param);
    }

    public boolean hasParam(String param) {
        return this.params.containsKey(param);
    }

    public HashMap<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, List<String>> headers) {
        this.headers = headers;
    }

    public List<String> getHeader(String header) {
        return this.headers.get(header);
    }

    public boolean hasHeader(String header) {
        return this.headers.containsKey(header);
    }

    public String getHost() {
        return host;
    }

    public Request setHost(String host) {
        this.host = host;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public Request setUri(String uri) {
        this.uri = uri;
        return this;
    }
}
