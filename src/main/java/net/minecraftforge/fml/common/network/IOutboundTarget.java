package net.minecraftforge.fml.common.network;

import io.netty.channel.ChannelHandlerContext;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.List;

public interface IOutboundTarget {
    /**
     * Returns true if the side is allowed to use this as an outbound target.
     * @param side The side
     * @return true if the side is allowed to use this as an outbound target
     */
    boolean isSideAllowed(Side side);

    /**
     * Validates the arguments object and throws a RuntimeException if it is invalid.
     * @param args The arguments object
     */
    void validateArgs(Object args);

    /**
     * Produces a list of NetworkDispatchers that the packet can be sent to.
     * The list may be null, which is equivalent to an empty list.
     * @param args The arguments object
     * @param context The channel handler context
     * @param packet The packet that will be sent
     * @return A list of NetworkDispatchers. May be null
     */
    @Nullable
    List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet);
}
