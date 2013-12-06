package cpw.mods.fml.common.network;


/**
 * Marker for packet handlers
 * @author cpw
 *
 */
public interface IPacketHandler<S extends NetworkSide> {
    S side();
}
