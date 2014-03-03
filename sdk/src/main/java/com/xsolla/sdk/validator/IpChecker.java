package com.xsolla.sdk.validator;

import com.xsolla.sdk.exception.*;
import com.xsolla.sdk.exception.SecurityException;
import org.apache.commons.net.util.SubnetUtils;
import java.util.ArrayList;
import java.util.Collection;

public class IpChecker {
    protected String[] subnets = {
            "94.103.26.176/29",
            "159.255.220.240/28",
            "185.30.20.16/29",
            "185.30.21.16/29"
    };

    protected Collection<SubnetUtils.SubnetInfo> subnetInfosWhiteList;

    public IpChecker() {
        this.subnetInfosWhiteList = new ArrayList<SubnetUtils.SubnetInfo>();
        for (String subnet : subnets) {
            this.subnetInfosWhiteList.add(new SubnetUtils(subnet).getInfo());

        }
    }

    /**
     *
     * @param clientIp
     */
    public void checkIp(String clientIp) throws com.xsolla.sdk.exception.SecurityException {
        Boolean clientIpInSubnets = false;
        for (SubnetUtils.SubnetInfo subnetInfo : this.subnetInfosWhiteList) {
            clientIpInSubnets = clientIpInSubnets || subnetInfo.isInRange(clientIp);
        }
        if (!clientIpInSubnets) {
            throw new SecurityException("Your IP address is not in white list.");
        }
    }
}
