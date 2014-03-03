package com.xsolla.sdk.protocol.command.response;

public class ShoppingCartCommandResponse extends CommandResponse {

    public static final int CODE_FATAL_ERROR = 40;
    public static final int CODE_TEMPORARY_ERROR = 30;
    public static final int CODE_INVALID_REQUEST = 20;

    protected String description;

    public ShoppingCartCommandResponse() {
        this.description = "";
    }

    public String getComment() {
        return description;
    }

    public void setComment(String comment) {
        this.description = comment;
    }

    public void setResultAsInvalidSign() {
        this.result = CODE_INVALID_REQUEST;
    }

    public void setResultAsInvalidRequest() {
        this.result = CODE_INVALID_REQUEST;
    }

    public void setResultAsUnprocessableRequest() {
        this.result = CODE_FATAL_ERROR;
    }

    public void setResultAsTemporaryServerError() {
        this.result = CODE_TEMPORARY_ERROR;
    }

    protected String getPartXml() {
        return new StringBuilder(super.getPartXml())
                .append("<description>")
                .append(this.description)
                .append("</description>")
                .toString();
    }
}
