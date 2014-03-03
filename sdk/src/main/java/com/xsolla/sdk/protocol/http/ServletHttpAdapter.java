package com.xsolla.sdk.protocol.http;


import com.xsolla.sdk.protocol.request.Request;
import com.xsolla.sdk.protocol.response.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class ServletHttpAdapter implements IHttpAdapter {

    HttpServletRequest httpServletRequest;
    HttpServletResponse httpServletResponse;

    public ServletHttpAdapter(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    /**
     * XSolla SDK works only with GET method
     * @return
     */
    public Request getRequest() {
        Request request = new Request();
        request.setClientIP(this.httpServletRequest.getRemoteAddr());
        request.setHost(this.httpServletRequest.getRemoteHost());
        String httpMethod = this.httpServletRequest.getMethod();
        if (httpMethod.equals("GET")) {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            Enumeration<String> paramNames = this.httpServletRequest.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = this.httpServletRequest.getParameter(paramName);
                params.put(paramName, paramValue);
            }
            request.setParams(params);
        }
        request.setUri(this.httpServletRequest.getRequestURI());
        request.setHeaders(this.headersToHashMap(this.httpServletRequest));
        return request;
    }

    public void sendResponse(Response response) {
        try {
            this.httpServletResponse.resetBuffer();
            this.httpServletResponse.setContentType(response.getContentType());
            PrintWriter output = this.httpServletResponse.getWriter();
            output.print(response.getContent());
        } catch (IOException e) {}
    }

    /**
     * Convert HttpServletRequest headers to format, used in com.xsolla.sdk.protocol.request.Request
     * @param httpServletRequest
     * @return
     */
    public HashMap<String, List<String>> headersToHashMap(HttpServletRequest httpServletRequest) {
        HashMap<String, List<String>>  headers = new HashMap<String, List<String>>();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            List<String> header = new ArrayList<String>();
            Enumeration<String> headerValues = this.httpServletRequest.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                header.add(headerValue);
            }
            headers.put(headerName, header);
        }
        return headers;
    }

}
