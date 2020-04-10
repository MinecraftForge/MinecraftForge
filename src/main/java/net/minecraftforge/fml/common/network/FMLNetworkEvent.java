/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

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
    private final T handler;
    private final NetworkManager manager;
    private final Class<T> type;

    FMLNetworkEvent(T thing, Class<T> type, NetworkManager manager)
    {
        this.handler = thing;
        this.type = type;
        this.manager = manager;
    }
    
    public Class<T> getHandlerType()
    {
        return getType();
    }

    public T getHandler()
    {
        return handler;
    }

    public NetworkManager getManager()
    {
        return manager;
    }

    public Class<T> getType()
    {
        return type;
    }

    /**
     * Fired at the client when a client connects to a server
     */
    public static class ClientConnectedToServerEvent extends FMLNetworkEvent<INetHandlerPlayClient> {
        private final boolean isLocal;
        private final String connectionType;
        public ClientConnectedToServerEvent(NetworkManager manager, String connectionType)
        {
            super((INetHandlerPlayClient) manager.getNetHandler(), INetHandlerPlayClient.class, manager);
            this.isLocal = manager.isLocalChannel();
            this.connectionType = connectionType;
        }

        public boolean isLocal()
        {
            return isLocal;
        }

        public String getConnectionType()
        {
            return connectionType;
        }
    }

    /**
     * Fired at the server when a client connects to the server.
     *
     * @author cpw
     *
     */
    public static class ServerConnectionFromClientEvent extends FMLNetworkEvent<INetHandlerPlayServer> {
        private final boolean isLocal;
        public ServerConnectionFromClientEvent(NetworkManager manager)
        {
            super((INetHandlerPlayServer) manager.getNetHandler(), INetHandlerPlayServer.class, manager);
            this.isLocal = manager.isLocalChannel();
        }

        public boolean isLocal()
        {
            return isLocal;
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
        private final ImmutableSet<String> registrations;
        private final String operation;
        private final Side side;
        public CustomPacketRegistrationEvent(NetworkManager manager, Set<String> registrations, String operation, Side side, Class<S> type)
        {
            super(type.cast(manager.getNetHandler()), type, manager);
            this.registrations = ImmutableSet.copyOf(registrations);
            this.side = side;
            this.operation = operation;
        }

        public ImmutableSet<String> getRegistrations()
        {
            return registrations;
        }

        public String getOperation()
        {
            return operation;
        }

        public Side getSide()
        {
            return side;
        }
    }

    public static abstract class CustomPacketEvent<S extends INetHandler> extends FMLNetworkEvent<S> {
        private final FMLProxyPacket packet;

        private FMLProxyPacket reply;
        CustomPacketEvent(S thing, Class<S> type, NetworkManager manager, FMLProxyPacket packet)
        {
            super(thing, type, manager);
            this.packet = packet;
        }

        public abstract Side side();

        /**
         * The packet that generated the event
         */
        public FMLProxyPacket getPacket()
        {
            return packet;
        }

        /**
         * Set this packet to reply to the originator
         */
        public FMLProxyPacket getReply()
        {
            return reply;
        }

        public void setReply(FMLProxyPacket reply)
        {
            this.reply = reply;
        }
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
        private final Object wrappedEvent;
        public CustomNetworkEvent(Object wrappedEvent)
        {
            this.wrappedEvent = wrappedEvent;
        }

        public Object getWrappedEvent()
        {
            return wrappedEvent;
        }
    }
}
