package com.villa.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IPUtil {
    /**
     * 获取客户端的IP地址
     * 方法是：request.getRemoteAddr()，这种方法在大部分情况下都是有效的。
     * 但是在通过了Apache,Squid等反向代理软件就不能获取到客户端的真实IP地址了，如果通过了多级反向代理的话，
     * X-Forwarded-For的值并不止一个，而是一串IP值， 是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * 例如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100 用户真实IP为： 192.168.1.110
     * @param request
     * @return
     */
    public static String getIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)){
            //这一步可能得到的是127.0.0.1  如果需要内网地址 通过网卡获取即可
            ip = request.getRemoteAddr();
        }
        // 对于通过多个代理的情况， 第一个IP为客户端真实IP,多个IP按照','分割 "***.***.***.***".length() = 15
        int ipMaxLength = 15;
        if (ip != null && ip.length() > ipMaxLength) {
            if(ip.indexOf(",")!=-1){
                return ip.substring(0, ip.indexOf(","));
            }
            if(ip.indexOf(":")!=-1){
                return ip;
            }
        }
        return ip;
    }
    /** 获取内网IP */
    public static String getLocalIp() throws SocketException {
        String ip = "";
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface anInterface = en.nextElement();
            String name = anInterface.getName();
            if (!name.contains("docker") && !name.contains("lo")) {
                for (Enumeration<InetAddress> enumIpAddr = anInterface.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    //获得IP
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ipaddress = inetAddress.getHostAddress().toString();
                        if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                            if(!"127.0.0.1".equals(ip)){
                                return ipaddress;
                            }
                        }
                    }
                }
            }
        }
        return ip;
    }
}
