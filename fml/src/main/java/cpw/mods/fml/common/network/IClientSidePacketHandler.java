package cpw.mods.fml.common.network;

import net.minecraft.network.play.server.S3FPacketCustomPayload;

/**
 * Handle packets <em>from</em> the server at the client.
 *
 * @author cpw
 *
 */
public interface IClientSidePacketHandler extends IPacketHandler {
    /**
     * Handle the custompayload packet
     * @param packet
     */
    public void handleCustomPayload(S3FPacketCustomPayload packet);
}
