package cpw.mods.fml.common.network;

import net.minecraft.network.INetHandler;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.relauncher.Side;

/**
 * This message is sent through all channels affected by a currently occurring handshake. It is guaranteed to
 * be able to send a custom payload packet, however, interaction with minecraft and world state is NOT assured
 * as it is likely this is fired on a netty handler thread, not a world processing thread.
 *
 * If you wish to send an outbound message through your channel, bind the {@link FMLOutboundHandler#FML_MESSAGETARGET}
 * property of your channel to the supplied dispatcher.
 * @author cpw
 *
 */
public class NetworkHandshakeEstablished {
    public final NetworkDispatcher dispatcher;
    public final Side side;
    public final INetHandler netHandler;

    public NetworkHandshakeEstablished(NetworkDispatcher dispatcher, INetHandler netHandler, Side origin)
    {
        this.netHandler = netHandler;
        this.dispatcher = dispatcher;
        this.side = origin;
    }
}