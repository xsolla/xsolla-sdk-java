package com.xsolla.sdk;

public class Version {

    protected static final String name = "sdk-java";
    protected static final String version = "1.0.1";

    public String toString() {
        return String.format("%s/%s jre/%s", name, version, System.getProperty("java.version"));
    }
}
