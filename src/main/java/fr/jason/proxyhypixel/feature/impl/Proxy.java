package fr.jason.proxyhypixel.feature.impl;

import fr.jason.proxyhypixel.ProxyHypixel;
import fr.jason.proxyhypixel.config.ProxyHypixelConfig;
import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.socket.oio.OioSocketChannel;

import java.net.*;
import java.util.Objects;

public class Proxy {
    private static Proxy instance;

    public static Proxy getInstance() {
        if (instance == null) {
            instance = new Proxy();
        }
        return instance;
    }

    public enum ProxyType {
        SOCKS,
        HTTP,
    }

    public void setProxy(boolean enabled, String host, ProxyType type, String username, String password) {
        ProxyHypixelConfig.proxyEnabled = enabled;
        ProxyHypixelConfig.proxyAddress = host;
        ProxyHypixelConfig.proxyType = type;
        if (!Objects.equals(username, "Username"))
            ProxyHypixelConfig.proxyUsername = username;
        if (!Objects.equals(password, "Password"))
            ProxyHypixelConfig.proxyPassword = password;
        ProxyHypixel.config.save();
    }

    public java.net.Proxy getProxy() {
        if (!ProxyHypixelConfig.proxyEnabled) return null;
        String addressStr = ProxyHypixelConfig.proxyAddress.split(":")[0];
        int port = Integer.parseInt(ProxyHypixelConfig.proxyAddress.split(":")[1]);
        InetSocketAddress address;
        try {
            address = new InetSocketAddress(InetAddress.getByName(addressStr), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }

        if (ProxyHypixelConfig.proxyUsername != null && ProxyHypixelConfig.proxyPassword != null)
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(ProxyHypixelConfig.proxyUsername, ProxyHypixelConfig.proxyPassword.toCharArray());
                }
            });

        switch (ProxyHypixelConfig.proxyType) {
            case SOCKS:
                return new java.net.Proxy(java.net.Proxy.Type.SOCKS, address);
            case HTTP:
                return new java.net.Proxy(java.net.Proxy.Type.HTTP, address);
            default:
                return null;
        }
    }

    public static class ProxyOioChannelFactory implements ChannelFactory<OioSocketChannel> {

        private final java.net.Proxy proxy;

        public ProxyOioChannelFactory(java.net.Proxy proxy) {
            this.proxy = proxy;
        }

        @Override
        public OioSocketChannel newChannel() {
            return new OioSocketChannel(new Socket(proxy));
        }
    }
}
