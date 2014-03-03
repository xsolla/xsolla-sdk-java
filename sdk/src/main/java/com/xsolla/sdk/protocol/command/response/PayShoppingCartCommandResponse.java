package com.xsolla.sdk.protocol.command.response;

public class PayShoppingCartCommandResponse extends ShoppingCartCommandResponse {

    protected String id_shop;

    public String getIdShop() {
        return id_shop;
    }

    public void setIdShop(String id_shop) {
        this.id_shop = id_shop;
    }

    protected String getPartXml() {
        StringBuilder xml = new StringBuilder(super.getPartXml());
        if (this.id_shop != null) {
            xml.append("<id_shop>").append(this.id_shop).append("</id_shop>");
        }
        return xml.toString();
    }
}
