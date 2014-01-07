package cpw.mods.fml.common.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import cpw.mods.fml.common.eventhandler.Event;

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
}
