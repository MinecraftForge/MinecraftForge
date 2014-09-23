package net.minecraftforge.fml.common.network.simpleimpl;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Context for the {@link IMessageHandler}
 *
 * @author cpw
 *
 */
public class MessageContext {
    /**
     * The {@link INetHandler} for this message. It could be a client or server handler, depending
     * on the {@link #side} recieved.
     */
    public final INetHandler netHandler;

    /**
     * The Side this message has been received on
     */
    public final Side side;
    /**
     * @param netHandler
     */
    MessageContext(INetHandler netHandler, Side side)
    {
        this.netHandler = netHandler;
        this.side = side;
    }

    public NetHandlerPlayServer getServerHandler()
    {
        return (NetHandlerPlayServer) netHandler;
    }

    public NetHandlerPlayClient getClientHandler()
    {
        return (NetHandlerPlayClient) netHandler;
    }
}
