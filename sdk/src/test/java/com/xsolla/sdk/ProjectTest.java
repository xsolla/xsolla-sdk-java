package com.xsolla.sdk;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ProjectTest{

    private static final int PROJECT_ID = 1234;
    private static final String SECRET_KEY = "mykey";

    @Test
    public void testGetters() {
        Project project = new Project(PROJECT_ID, SECRET_KEY);
        assertEquals(PROJECT_ID, project.getProjectId());
        assertEquals(SECRET_KEY, project.getSecretKey());
    }
}
