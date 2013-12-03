package cpw.mods.fml.common.network;

import net.minecraft.network.play.client.C17PacketCustomPayload;

/**
 * Handle packets <em>from</em> the client at the server.
 *
 * @author cpw
 *
 */
public interface IServerSidePacketHandler {
    public void handleCustomPayload(C17PacketCustomPayload packet);
}
