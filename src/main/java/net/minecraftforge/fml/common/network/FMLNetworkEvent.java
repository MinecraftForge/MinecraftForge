package net.minecraftforge.fml.common.network;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

public class FMLNetworkEvent<T extends INetHandler> extends Event {
    public final T handler;
    public final NetworkManager manager;
    private final Class<T> type;

    FMLNetworkEvent(T thing, Class<T> type, NetworkManager manager)
    {
        this.handler = thing;
        this.type = type;
        this.manager = manager;
    }
    
    public Class<T> getHandlerType()
    {
        return type;
    }
    
    /**
     * Fired at the client when a client connects to a server
     */
    public static class ClientConnectedToServerEvent extends FMLNetworkEvent<INetHandlerPlayClient> {
        public final boolean isLocal;
        public final String connectionType;
        public ClientConnectedToServerEvent(NetworkManager manager, String connectionType)
        {
            super((INetHandlerPlayClient) manager.getNetHandler(), INetHandlerPlayClient.class, manager);
            this.isLocal = manager.isLocalChannel();
            this.connectionType = connectionType;
        }
    }

    /**
     * Fired at the server when a client connects to the server.
     *
     * @author cpw
     *
     */
    public static class ServerConnectionFromClientEvent extends FMLNetworkEvent<INetHandlerPlayServer> {
        public final boolean isLocal;
        public ServerConnectionFromClientEvent(NetworkManager manager)
        {
            super((INetHandlerPlayServer) manager.getNetHandler(), INetHandlerPlayServer.class, manager);
            this.isLocal = manager.isLocalChannel();
        }
    }
    /**
     * Fired at the server when a client disconnects.
     *
     * @author cpw
     *
     */
    public static class ServerDisconnectionFromClientEvent extends FMLNetworkEvent<INetHandlerPlayServer> {
        public ServerDisconnectionFromClientEvent(NetworkManager manager)
        {
            super((INetHandlerPlayServer) manager.getNetHandler(), INetHandlerPlayServer.class, manager);
        }
    }
    /**
     * Fired at the client when the client is disconnected from the server.
     *
     * @author cpw
     *
     */
    public static class ClientDisconnectionFromServerEvent extends FMLNetworkEvent<INetHandlerPlayClient> {
        public ClientDisconnectionFromServerEvent(NetworkManager manager)
        {
            super((INetHandlerPlayClient) manager.getNetHandler(), INetHandlerPlayClient.class, manager);
        }
    }

    /**
     * Fired when the REGISTER/UNREGISTER for custom channels is received.
     *
     * @author cpw
     *
     * @param <S> The side
     */
    public static class CustomPacketRegistrationEvent<S extends INetHandler> extends FMLNetworkEvent<S> {
        public final ImmutableSet<String> registrations;
        public final String operation;
        public final Side side;
        public CustomPacketRegistrationEvent(NetworkManager manager, Set<String> registrations, String operation, Side side, Class<S> type)
        {
            super(type.cast(manager.getNetHandler()), type, manager);
            this.registrations = ImmutableSet.copyOf(registrations);
            this.side = side;
            this.operation = operation;
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

    /**
     * Fired when a custom packet is received on the client for the channel
     * @author cpw
     *
     */
    public static class ClientCustomPacketEvent extends CustomPacketEvent<INetHandlerPlayClient> {
        public ClientCustomPacketEvent(NetworkManager manager, FMLProxyPacket packet)
        {
            super((INetHandlerPlayClient) manager.getNetHandler(), INetHandlerPlayClient.class, manager, packet);
        }

        @Override
        public Side side()
        {
            return Side.CLIENT;
        }
    }

    /**
     * Fired when a custom packet is received at the server for the channel
     * @author cpw
     *
     */
    public static class ServerCustomPacketEvent extends CustomPacketEvent<INetHandlerPlayServer> {
        public ServerCustomPacketEvent(NetworkManager manager, FMLProxyPacket packet)
        {
            super((INetHandlerPlayServer) manager.getNetHandler(), INetHandlerPlayServer.class, manager, packet);
        }

        @Override
        public Side side()
        {
            return Side.SERVER;
        }
    }

    /**
     * Fired when a custom event, such as {@link NetworkHandshakeEstablished} is fired for the channel
     *
     * @author cpw
     *
     */
    public static class CustomNetworkEvent extends Event {
        public final Object wrappedEvent;
        public CustomNetworkEvent(Object wrappedEvent)
        {
            this.wrappedEvent = wrappedEvent;
        }
    }
}
