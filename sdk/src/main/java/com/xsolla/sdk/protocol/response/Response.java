package com.xsolla.sdk.protocol.response;


import java.util.HashMap;

public class Response {

    protected HashMap<String, String> headers;
    protected String content;
    protected String contentType;

    public Response() {
        this.headers = new HashMap<String, String>();
        this.headers.put("X-Xsolla-SDK", "java-sdk/0.1 jre/" + System.getProperty("java.version"));
        this.content = "";
        this.contentType = "Content-type: text/xml; charset=\"UTF-8\"";
    }

    public String getContent() {
        return this.content;
    }

    public Response setContent(String content) {
        this.content = content;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public Response setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }
}
