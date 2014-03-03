package com.xsolla.sdk;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {

    @Test
    public void testProperties() {
        User user = new User("myv1").setV2("myv2").setV3("myv3").setEmail("myemail").setPhone("myphone");

        assertEquals("myv1", user.getV1());
        assertEquals("myv2", user.getV2());
        assertEquals("myv3", user.getV3());
        assertEquals("myemail", user.getEmail());
        assertEquals("myphone", user.getPhone());

        user.setV1("myv1new");
        assertEquals("myv1new", user.getV1());
    }
}
