package cpw.mods.fml.common.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.Map.Entry;
import net.minecraft.network.Packet;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.FMLOutboundHandler.OutboundTarget;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;

/**
 * Utility wrapper around {@link EmbeddedChannel}. Provides some convenience methods
 * associated with the specific needs of FML network handling.
 *
 * @author cpw
 *
 */
public class FMLEmbeddedChannel extends EmbeddedChannel {
    public FMLEmbeddedChannel(String channelName, Side source, ChannelHandler... handlers)
    {
        this(Loader.instance().activeModContainer(), channelName, source, handlers);
    }
    public FMLEmbeddedChannel(ModContainer container, String channelName, Side source, ChannelHandler... handlers)
    {
        super(handlers);
        this.attr(NetworkRegistry.FML_CHANNEL).set(channelName);
        this.attr(NetworkRegistry.CHANNEL_SOURCE).set(source);
        this.attr(NetworkRegistry.MOD_CONTAINER).setIfAbsent(container);
        this.pipeline().addFirst("fml:outbound",new FMLOutboundHandler());
    }


    /**
     * Utility method to generate a regular packet from a custom packet. Basically, it writes the packet through the
     * outbound side which should have a message to message codec present (such as {@link FMLIndexedMessageToMessageCodec},
     * transforming from mod packets to standard {@link FMLProxyPacket}s.
     *
     * This is mostly useful in cases where vanilla expects a packet, such as the TileEntity getDescriptionPacket.
     *
     * @param object The inbound packet
     * @return A Packet suitable for passing to vanilla network code.
     */
    public Packet generatePacketFrom(Object object)
    {
        OutboundTarget outboundTarget = attr(FMLOutboundHandler.FML_MESSAGETARGET).getAndSet(OutboundTarget.NOWHERE);
        writeOutbound(object);
        Packet pkt = (Packet) outboundMessages().poll();
        attr(FMLOutboundHandler.FML_MESSAGETARGET).set(outboundTarget);
        return pkt;
    }

    public String findChannelHandlerNameForType(Class<? extends ChannelHandler> type)
    {
        String targetName = null;
        for (Entry<String, ChannelHandler> entry : pipeline())
        {
            if (type.isInstance(entry.getValue()))
            {
                targetName = entry.getKey();
                break;
            }
        }
        return targetName;
    }
}