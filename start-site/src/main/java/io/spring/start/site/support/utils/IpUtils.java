package io.spring.start.site.support.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IpUtils {

    private static final int IP4_SEGMENT = 4;

    /**
     * 获取IP地址
     */
    public static String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("获取服务ip地址失败", e);
            return "";
        }
    }

    public static String randomIp() {
        StringBuilder ip = new StringBuilder();
        for (int i = 0; i < IP4_SEGMENT; i++) {
            ip.append(new SecureRandom().nextInt(255));
            if (i != IP4_SEGMENT - 1) {
                ip.append(".");
            }
        }
        return ip.toString();
    }

    public static int ip2Int(String ip) {
        String[] ipArray = ip.split("\\.");
        int intIp = 0;
        int j = ipArray.length - 1;
        for (String s : ipArray) {
            int ipSegment = Integer.parseInt(s) << 8 * j--;
            intIp = intIp | ipSegment;
        }
        return intIp;
    }

    /**
     * 10.118.32.94
     * -> int
     * -> 94.32.118.10
     */
    public static int ip2IntReverse(String ip) {
        String[] ipArray = ip.split("\\.");
        int intIp = 0;
        for (int i = 0; i < ipArray.length; i++) {
            int ipSegment = Integer.parseInt(ipArray[i]) << 8 * i;
            intIp = intIp | ipSegment;
        }
        return intIp;
    }

}
