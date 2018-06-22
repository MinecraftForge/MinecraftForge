package net.minecraftforge.fml.test;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.apache.logging.log4j.Logger;

@Mod(modid = ForgeNetworkTestMod.MOD_ID, name = ForgeNetworkTestMod.MOD_ID, version = "1.0", acceptableRemoteVersions = "*")
public class ForgeNetworkTestMod
{
    private static final boolean ENABLED = true;
    public static final String MOD_ID = "forge_network_test";

    private FMLEventChannel channel;
    private Logger logger;

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent e)
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(this);
            channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(MOD_ID);
            channel.register(this);
            logger = e.getModLog();
        }
    }

    @SubscribeEvent
    public void onConnectionFromClient(FMLNetworkEvent.ServerConnectionFromClientEvent event)
    {
        if (channel != null && event.getHandler() instanceof NetHandlerPlayServer)
        {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeByte(0);
            channel.sendTo(new FMLProxyPacket(buffer, MOD_ID), ((NetHandlerPlayServer) event.getHandler()).player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent e)
    {
        if (channel == null)
        {
            return;
        }
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeByte(0);
        channel.sendTo(new FMLProxyPacket(buffer, MOD_ID), (EntityPlayerMP) e.player); // disconnects vanilla clients in 1.11
    }

    @SubscribeEvent
    public void onClientRecievedPacket(FMLNetworkEvent.ClientCustomPacketEvent event)
    {
        logger.info("Received packet from server");
    }
}
