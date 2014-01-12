package cpw.mods.fml.common.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;

public class FMLNetworkEvent<T extends INetHandler> extends Event {
    public final T handler;
    public final NetworkManager manager;
    @SuppressWarnings("unused")
    private final Class<T> type;

    FMLNetworkEvent(T thing, Class<T> type, NetworkManager manager)
    {
        this.handler = thing;
        this.type = type;
        this.manager = manager;
    }
    public static class ClientConnectedToServerEvent extends FMLNetworkEvent<INetHandlerPlayClient> {
        public final boolean isLocal;
        public final String connectionType;
        public ClientConnectedToServerEvent(NetworkManager manager, String connectionType)
        {
            super((INetHandlerPlayClient) manager.func_150729_e(), INetHandlerPlayClient.class, manager);
            this.isLocal = manager.func_150731_c();
            this.connectionType = connectionType;
        }
    }

    public static class ServerConnectionFromClientEvent extends FMLNetworkEvent<INetHandlerPlayServer> {
        public final boolean isLocal;
        public ServerConnectionFromClientEvent(NetworkManager manager)
        {
            super((INetHandlerPlayServer) manager.func_150729_e(), INetHandlerPlayServer.class, manager);
            this.isLocal = manager.func_150731_c();
        }
    }
    public static class ServerDisconnectionFromClientEvent extends FMLNetworkEvent<INetHandlerPlayServer> {
        public ServerDisconnectionFromClientEvent(NetworkManager manager)
        {
            super((INetHandlerPlayServer) manager.func_150729_e(), INetHandlerPlayServer.class, manager);
        }
    }
    public static class ClientDisconnectionFromServerEvent extends FMLNetworkEvent<INetHandlerPlayClient> {
        public ClientDisconnectionFromServerEvent(NetworkManager manager)
        {
            super((INetHandlerPlayClient) manager.func_150729_e(), INetHandlerPlayClient.class, manager);
        }
    }

    public static abstract class CustomPacketEvent<S extends INetHandler> extends FMLNetworkEvent<S> {
        /**
         * The packet that generated the event
         */
        public final FMLProxyPacket packet;

        /**
         * Set this packet to reply to the originator
         */
        public FMLProxyPacket reply;
        CustomPacketEvent(S thing, Class<S> type, NetworkManager manager, FMLProxyPacket packet)
        {
            super(thing, type, manager);
            this.packet = packet;
        }

        public abstract Side side();
    }

    public static class ClientCustomPacketEvent extends CustomPacketEvent<INetHandlerPlayClient> {
        public ClientCustomPacketEvent(NetworkManager manager, FMLProxyPacket packet)
        {
            super((INetHandlerPlayClient) manager.func_150729_e(), INetHandlerPlayClient.class, manager, packet);
        }

        @Override
        public Side side()
        {
            return Side.CLIENT;
        }
    }

    public static class ServerCustomPacketEvent extends CustomPacketEvent<INetHandlerPlayServer> {
        public ServerCustomPacketEvent(NetworkManager manager, FMLProxyPacket packet)
        {
            super((INetHandlerPlayServer) manager.func_150729_e(), INetHandlerPlayServer.class, manager, packet);
        }

        @Override
        public Side side()
        {
            return Side.SERVER;
        }
    }
}
