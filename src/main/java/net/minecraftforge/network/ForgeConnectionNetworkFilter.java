package net.minecraftforge.network;

import java.util.List;

import io.netty.channel.ChannelHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraftforge.fml.network.NetworkHooks;

import com.google.common.collect.ImmutableMap;

/**
 * Network filter for forge-forge connections.
 */
@ChannelHandler.Sharable
public class ForgeConnectionNetworkFilter extends VanillaPacketFilter
{

    public ForgeConnectionNetworkFilter()
    {
        super(
                ImmutableMap.of(
                        SUpdateRecipesPacket.class, ForgeConnectionNetworkFilter::splitPacket,
                        STagsListPacket.class, ForgeConnectionNetworkFilter::splitPacket
                )
        );
    }

    @Override
    protected boolean isNecessary(NetworkManager manager)
    {
        // not needed on local connections, because packets are not encoded to bytes there
        return !manager.isMemoryConnection() && !NetworkHooks.isVanillaConnection(manager);
    }

    private static void splitPacket(IPacket<?> packet, List<? super IPacket<?>> out)
    {
        VanillaPacketSplitter.appendPackets(
                ProtocolType.PLAY, PacketDirection.CLIENTBOUND, packet, out
        );
    }

}
