package fr.jason.proxyhypixel.mixin.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import fr.jason.proxyhypixel.config.ProxyHypixelConfig;
import fr.jason.proxyhypixel.feature.impl.Proxy;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetAddress;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {
    @Inject(method = "createNetworkManagerAndConnect", at = @At("HEAD"), cancellable = true)
    private static void createNetworkManagerAndConnect(InetAddress address, int serverPort, boolean useNativeTransport, CallbackInfoReturnable<NetworkManager> cir) {
        if (!ProxyHypixelConfig.proxyEnabled) {
            return;
        }
        final NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);

        Bootstrap bootstrap = new Bootstrap();

        EventLoopGroup eventLoopGroup;
        java.net.Proxy proxy = Proxy.getInstance().getProxy();
        eventLoopGroup = new OioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
        bootstrap.channelFactory(new Proxy.ProxyOioChannelFactory(proxy));

        bootstrap.group(eventLoopGroup).handler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel channel) {
                System.out.println("ILLEGAL CHANNEL INITIALIZATION: This should be patched to net/minecraft/network/NetworkManager$5!");
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException var3) {
                    var3.printStackTrace();
                }
                channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new MessageDeserializer2()).addLast("decoder", new MessageDeserializer(EnumPacketDirection.CLIENTBOUND)).addLast("prepender", new MessageSerializer2()).addLast("encoder", new MessageSerializer(EnumPacketDirection.SERVERBOUND)).addLast("packet_handler", networkmanager);
            }
        });

        bootstrap.connect(address, serverPort).syncUninterruptibly();

        cir.setReturnValue(networkmanager);
        cir.cancel();
    }
}
