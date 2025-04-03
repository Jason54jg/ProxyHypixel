package fr.jason.proxyhypixel.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.data.*;
import fr.jason.proxyhypixel.feature.impl.Proxy;

@SuppressWarnings({"unused", "DefaultAnnotationParam"})
public class ProxyHypixelConfig extends Config {

    //<editor-fold desc="PROXY">
    public static boolean proxyEnabled = false;
    public static String proxyAddress = "";
    public static String proxyUsername = "";
    public static String proxyPassword = "";
    public static Proxy.ProxyType proxyType = Proxy.ProxyType.HTTP;
    //</editor-fold>

    public ProxyHypixelConfig() {
        super(new Mod("Proxy Hypixel", ModType.HYPIXEL, "/proxyhypixel/icon-mod/proxy.png"), "/proxyhypixel/config.json");
        initialize();

        this.hideIf("configVersion", () -> true);
        save();
    }
}
