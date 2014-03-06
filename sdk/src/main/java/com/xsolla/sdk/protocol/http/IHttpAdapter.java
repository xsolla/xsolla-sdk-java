package com.xsolla.sdk.protocol.http;

import com.xsolla.sdk.protocol.request.Request;
import com.xsolla.sdk.protocol.response.Response;

public interface IHttpAdapter {

    /**
     * Convert WEB server request to com.xsolla.sdk.request.Request
     * If you WEB server software support JavaEE Servlets, use ServletHttpAdapter
     * @return Request
     */
    public Request getRequest();

    /**
     * Reply to Xsolla
     * If you WEB server software support JavaEE Servlets, use ServletHttpAdapter
     * @param response Response
     */
    public void sendResponse(Response response);
}
