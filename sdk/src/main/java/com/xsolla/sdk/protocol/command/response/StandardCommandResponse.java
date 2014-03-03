package com.xsolla.sdk.protocol.command.response;

public class StandardCommandResponse extends CommandResponse {

    public static final int CODE_TEMPORARY_ERROR = 1;
    public static final int CODE_INVALID_ORDER_DETAILS = 2;
    public static final int CODE_INVALID_SIGN = 3;
    public static final int CODE_INVALID_REQUEST = 4;
    public static final int CODE_FATAL_ERROR = 7;

    protected String comment;

    public StandardCommandResponse() {
        this.comment = "";
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setResultAsInvalidSign() {
        this.result = CODE_INVALID_SIGN;
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
                .append("<comment>")
                .append(this.comment)
                .append("</comment>")
                .toString();
    }
}