package com.xsolla.sdk.protocol.command.response;

public class PayStandardCommandResponse extends StandardCommandResponse {

    protected String idShop;

    public String getIdShop() {
        return idShop;
    }

    public void setIdShop(String idShop) {
        this.idShop = idShop;
    }

    protected String getPartXml() {
        StringBuilder xml = new StringBuilder(super.getPartXml());
        if (this.idShop != null) {
            xml.append("<id_shop>").append(this.idShop).append("</id_shop>");
        }
        return xml.toString();
    }
}
