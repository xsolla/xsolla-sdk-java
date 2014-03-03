package com.xsolla.sdk.protocol.command.response;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckCommandResponse extends StandardCommandResponse {

    public static final int CODE_USER_NOT_FOUND = 7;

    HashMap<String, String> specification;

    public int getCodeUserNotFound() {
        return CODE_USER_NOT_FOUND;
    }

    public HashMap<String, String> getSpecification() {
        return specification;
    }

    public void setSpecification(HashMap<String, String> specification) {
        this.specification = specification;
    }

    protected String getPartXml() {
        StringBuilder xml = new StringBuilder(super.getPartXml());
        if (this.specification == null || this.specification.isEmpty()) {
            return xml.toString();
        } else {
            xml.append("<specification>");
            for (String key : this.specification.keySet()) {
                xml.append("<").append(key).append(">");
                xml.append(this.specification.get(key));
                xml.append("</").append(key).append(">");
            }
            xml.append("</specification>");
            return xml.toString();
        }
    }
}
