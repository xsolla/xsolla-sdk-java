package com.xsolla.sdk.protocol.command.response;

abstract public class CommandResponse {

    final public static int CODE_SUCCESS = 0;

    protected int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    abstract public String getComment();

    abstract public void setComment(String comment);

    public int getCodeSuccess() {
        return CODE_SUCCESS;
    }

    public String toXml() {
        return new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<response>")
                .append(this.getPartXml())
                .append("</response>")
                .toString();
    }

    protected String getPartXml() {
        return new StringBuilder("<result>")
                .append(String.valueOf(this.result))
                .append("</result>")
                .toString();
    }

    abstract public void setResultAsInvalidSign();

    abstract public void setResultAsInvalidRequest();

    abstract public void setResultAsUnprocessableRequest();

    abstract public void setResultAsTemporaryServerError();
}