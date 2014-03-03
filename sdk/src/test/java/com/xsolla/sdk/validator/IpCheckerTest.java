package com.xsolla.sdk.validator;

import com.xsolla.sdk.exception.SecurityException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class IpCheckerTest {
    private IpChecker ipChecker;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        this.ipChecker = new IpChecker();
    }

    @Test
    public void testCheckIpWithWhitelist() throws SecurityException {
        this.ipChecker.checkIp("94.103.26.177");
        this.ipChecker.checkIp("159.255.220.243");
        this.ipChecker.checkIp("185.30.20.21");
        this.ipChecker.checkIp("185.30.21.22");
    }

    @Test
    public void testCheckIpSecureException() throws SecurityException {
        thrown.expect(SecurityException.class);
        thrown.expectMessage("Your IP address is not in white list.");
        this.ipChecker.checkIp("185.30.20.25");
    }
}
