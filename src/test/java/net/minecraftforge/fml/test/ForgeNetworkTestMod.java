package net.minecraftforge.fml.test;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
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
    private static final boolean ENABLED = false;
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
    public void onPlayerLogin(PlayerLoggedInEvent e)
    {
        if (ENABLED)
        {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeString("Welcome to the server!");
            channel.sendTo(new FMLProxyPacket(buffer, MOD_ID), (EntityPlayerMP) e.player); // disconnects vanilla clients in 1.11
            logger.info("Welcomed player to the server");
        }
    }

    @SubscribeEvent
    public void onPacketReceived(FMLNetworkEvent.ClientCustomPacketEvent e)
    {
        PacketBuffer payload = new PacketBuffer(e.getPacket().payload());
        logger.info("Received message from server: {}", payload.readString(64));

        PacketBuffer reply = new PacketBuffer(Unpooled.buffer());
        reply.writeString("Thanks!");
        e.setReply(new FMLProxyPacket(reply, MOD_ID));
        logger.info("Sent a reply");
    }

    @SubscribeEvent
    public void onPacketReceived(FMLNetworkEvent.ServerCustomPacketEvent e)
    {
        PacketBuffer payload = new PacketBuffer(e.getPacket().payload());
        logger.info("Received reply from client: {}", payload.readString(64));
    }
}
