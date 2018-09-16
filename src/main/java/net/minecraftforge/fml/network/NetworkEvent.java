/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml.network;

import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSidedProvider;

import java.util.function.Supplier;

public class NetworkEvent extends Event
{
    private final PacketBuffer payload;
    private final Supplier<Context> source;
    private NetworkEvent(ICustomPacket<?> payload, Supplier<Context> source)
    {
        this.payload = payload.getData();
        this.source = source;
    }

    public PacketBuffer getPayload()
    {
        return payload;
    }

    public Supplier<Context> getSource()
    {
        return source;
    }

    public static class ServerCustomPayloadEvent extends NetworkEvent
    {

        ServerCustomPayloadEvent(final ICustomPacket<?> payload, final Supplier<Context> source) {
            super(payload, source);
        }
    }
    public static class ClientCustomPayloadEvent extends NetworkEvent
    {
        ClientCustomPayloadEvent(final ICustomPacket<?> payload, final Supplier<Context> source) {
            super(payload, source);
        }
    }
    public static class ServerCustomPayloadLoginEvent extends ServerCustomPayloadEvent implements ILoginIndex {
        private final int index;

        ServerCustomPayloadLoginEvent(ICustomPacket<?> payload, Supplier<Context> source)
        {
            super(payload, source);
            this.index = payload.getIndex();
        }

        public int getIndex()
        {
            return index;
        }
    }

    public static class ClientCustomPayloadLoginEvent extends ClientCustomPayloadEvent implements ILoginIndex {
        private final int index;

        ClientCustomPayloadLoginEvent(ICustomPacket<?> payload, Supplier<Context> source)
        {
            super(payload, source);
            this.index = payload.getIndex();
        }

        public int getIndex()
        {
            return index;
        }
    }

    public interface ILoginIndex {
        int getIndex();
    }
    /**
     * Context for {@link NetworkEvent}
     */
    public static class Context
    {
        /**
         * The {@link NetworkManager} for this message.
         */
        private final NetworkManager networkManager;

        /**
         * The {@link NetworkDirection} this message has been received on.
         */
        private final NetworkDirection side;
        private boolean packetHandled;

        Context(NetworkManager netHandler, NetworkDirection side)
        {
            this.networkManager = netHandler;
            this.side = side;
        }

        public NetworkDirection getDirection() {
            return side;
        }

        public NetworkManager getNetworkManager() {
            return networkManager;
        }

        public void setPacketHandled(boolean packetHandled) {
            this.packetHandled = packetHandled;
        }

        public boolean getPacketHandled()
        {
            return packetHandled;
        }

        @SuppressWarnings("unchecked")
        public <V> ListenableFuture<V> enqueueWork(Runnable runnable) {
            return (ListenableFuture<V>)LogicalSidedProvider.WORKQUEUE.<IThreadListener>get(getDirection().getLogicalSide()).addScheduledTask(runnable);
        }
    }
}
