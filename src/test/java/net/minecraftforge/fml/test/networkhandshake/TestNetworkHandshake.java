package net.minecraftforge.fml.test.networkhandshake;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.Packet;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static org.junit.Assert.assertEquals;

/**
 * Created by cpw on 10/06/17.
 */
public class TestNetworkHandshake
{
    private static final Packet<?>[] vanillapackets = new Packet[] {
            new SPacketLoginSuccess(new GameProfile(null, "cheese")), new SPacketJoinGame(1, GameType.NOT_SET, false, 0, EnumDifficulty.PEACEFUL, 100, WorldType.DEFAULT, false)
    };

    private NetworkSystem listener;
    public static NetworkManager client;
    public static NetworkManager server;
    private CyclicBarrier barrier;

    @BeforeClass
    public static void setupLogging() {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.add(builder.newFilter("MarkerFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL).addAttribute("marker", "NETWORK"));
        builder.add(builder.newFilter("MarkerFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL).addAttribute("marker", "PACKET_RECEIVED"));
        builder.add(builder.newFilter("MarkerFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL).addAttribute("marker", "PACKET_SENT"));
        Configurator.initialize(builder.build());
        Configurator.setRootLevel(Level.DEBUG);
    }
    @Before
    public void setup() throws IOException, NoSuchFieldException, IllegalAccessException, InterruptedException
    {
        barrier = new CyclicBarrier(2);
        final MinecraftServer minecraftServer = new MinecraftServer(Paths.get(".").toFile(), null, null, null, null, null, null)
        {
            @Override
            public boolean init() throws IOException
            {
                return false;
            }

            @Override
            public boolean canStructuresSpawn()
            {
                return false;
            }

            @Override
            public GameType getGameType()
            {
                return null;
            }

            @Override
            public EnumDifficulty getDifficulty()
            {
                return null;
            }

            @Override
            public boolean isHardcore()
            {
                return false;
            }

            @Override
            public int getOpPermissionLevel()
            {
                return 0;
            }

            @Override
            public boolean shouldBroadcastRconToOps()
            {
                return false;
            }

            @Override
            public boolean shouldBroadcastConsoleToOps()
            {
                return false;
            }

            @Override
            public boolean isDedicatedServer()
            {
                return false;
            }

            @Override
            public boolean shouldUseNativeTransport()
            {
                return false;
            }

            @Override
            public boolean isCommandBlockEnabled()
            {
                return false;
            }

            @Override
            public String shareToLAN(GameType type, boolean allowCheats)
            {
                return null;
            }
        };
        listener = minecraftServer.getNetworkSystem();
        listener.addLanEndpoint(InetAddress.getLocalHost(), 54321);
        final Field networkManagers = listener.getClass().getDeclaredField("networkManagers");
        networkManagers.setAccessible(true);
        final List<NetworkManager> nmList = (List<NetworkManager>)networkManagers.get(listener);
        client = NetworkManager.createNetworkManagerAndConnect(InetAddress.getLocalHost(), 54321, true);
        while (nmList.isEmpty())
        {
            Thread.sleep(10);
        }

        server = nmList.get(0);
        Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {INetHandlerLoginClient.class, INetHandlerPlayClient.class}, (proxyobj, method, args) ->
        {
            if ("toString".equals(method.getName())) {
                return "PROXY";
            }
            System.out.println(method.getName());
            barrier.await();
            return null;
        });
        client.setNetHandler((INetHandler)proxy);
        while (!client.isChannelOpen() || !server.isChannelOpen()) {
            Thread.sleep(10);
        }
        client.setConnectionState(EnumConnectionState.LOGIN);
        server.setConnectionState(EnumConnectionState.LOGIN);
    }
    @Test
    public void testNetworkFlow() throws InterruptedException, BrokenBarrierException
    {
        assertEquals(EnumConnectionState.LOGIN, client.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get());
        assertEquals(EnumConnectionState.LOGIN, server.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get());
        server.sendPacket(vanillapackets[0]);
        barrier.await();
        client.setConnectionState(EnumConnectionState.PLAY);
        server.sendPacket(vanillapackets[1]);
    }
}
