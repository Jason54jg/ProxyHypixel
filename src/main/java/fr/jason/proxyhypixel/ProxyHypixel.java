package fr.jason.proxyhypixel;

import fr.jason.proxyhypixel.config.ProxyHypixelConfig;
import fr.jason.proxyhypixel.event.MillisecondEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod(modid = "proxyhypixel", useMetadata = true)
public class ProxyHypixel {
    public static final String VERSION = "%%VERSION%%";
    public static ProxyHypixelConfig config;
    public static boolean isDebug = false;
    private final Minecraft mc = Minecraft.getMinecraft();
    public static File jarFile = null;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        jarFile = event.getSourceFile();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        initializeFields();
        initializeListeners();

        mc.gameSettings.pauseOnLostFocus = false;
        mc.gameSettings.gammaSetting = 1000;
        isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp");

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> MinecraftForge.EVENT_BUS.post(new MillisecondEvent()), 0, 1, TimeUnit.MILLISECONDS);
    }

    private void initializeFields() {
        config = new ProxyHypixelConfig();
    }

    private void initializeListeners() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
