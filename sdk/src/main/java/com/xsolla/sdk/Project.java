package com.xsolla.sdk;


public class Project {
    protected int projectId;
    protected String secretKey;

    public Project(int projectId, String secretKey) {
        this.projectId = projectId;
        this.secretKey = secretKey;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
