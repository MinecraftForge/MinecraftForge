package cpw.mods.fml.common.network;

import net.minecraft.network.play.client.C17PacketCustomPayload;
import cpw.mods.fml.common.network.NetworkSide.ServerSide;

/**
 * Handle packets <em>from</em> the client at the server.
 *
 * @author cpw
 *
 */
public abstract class ServerSidePacketHandler implements IPacketHandler<ServerSide> {
    @Override
    public ServerSide side()
    {
        return NetworkSide.SERVER;
    }

    public abstract void handleCustomPayload(C17PacketCustomPayload packet);
}
