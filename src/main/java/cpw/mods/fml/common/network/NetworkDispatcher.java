package cpw.mods.fml.common.network;

import io.netty.util.AttributeKey;
import net.minecraft.network.NetworkManager;

import com.google.common.collect.ListMultimap;

public class NetworkDispatcher {
    private static final AttributeKey<NetworkDispatcher> FML_DISPATCHER = new AttributeKey<NetworkDispatcher>("fml:dispatcher");
    private static final AttributeKey<ListMultimap<String,IPacketHandler>> FML_PACKET_HANDLERS = new AttributeKey<ListMultimap<String,IPacketHandler>>("fml:packet_handlers");

    public static NetworkDispatcher get(NetworkManager manager)
    {
        return manager.channel().attr(FML_DISPATCHER).get();
    }
}
