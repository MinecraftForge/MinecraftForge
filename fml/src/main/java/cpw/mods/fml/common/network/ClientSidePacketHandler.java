package cpw.mods.fml.common.network;

import cpw.mods.fml.common.network.NetworkSide.ClientSide;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

/**
 * Handle packets <em>from</em> the server at the client.
 *
 * @author cpw
 *
 */
public abstract class ClientSidePacketHandler implements IPacketHandler<ClientSide> {
    @Override
    public ClientSide side()
    {
        return NetworkSide.CLIENT;
    }
    /**
     * Handle the custompayload packet
     * @param packet
     */
    public abstract void handleCustomPayload(S3FPacketCustomPayload packet);
}
