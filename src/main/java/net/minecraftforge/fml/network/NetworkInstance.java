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

import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventListener;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class NetworkInstance
{
    public String getChannelName()
    {
        return channelName.toString();
    }

    public enum NetworkSide {
        PLAYSERVER(NetworkEvent.ServerCustomPayloadEvent::new),
        PLAYCLIENT(NetworkEvent.ClientCustomPayloadEvent::new),
        LOGINSERVER(NetworkEvent.ServerCustomPayloadEvent::new),
        LOGINCLIENT(NetworkEvent.ClientCustomPayloadEvent::new);

        private final BiFunction<PacketBuffer, NetworkManager, NetworkEvent> eventSupplier;

        NetworkSide(BiFunction<PacketBuffer, NetworkManager, NetworkEvent> eventSupplier)
        {
            this.eventSupplier = eventSupplier;
        }

        public NetworkEvent getEvent(final PacketBuffer buffer, final NetworkManager manager) {
            return this.eventSupplier.apply(buffer, manager);
        }

    }
    private final ResourceLocation channelName;
    private final Supplier<String> networkProtocolVersion;
    private final Predicate<String> clientAcceptedVersions;
    private final Predicate<String> serverAcceptedVersions;
    private final IEventBus networkEventBus;

    NetworkInstance(ResourceLocation channelName, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions)
    {
        this.channelName = channelName;
        this.networkProtocolVersion = networkProtocolVersion;
        this.clientAcceptedVersions = clientAcceptedVersions;
        this.serverAcceptedVersions = serverAcceptedVersions;
        this.networkEventBus = IEventBus.create(this::handleError);
    }

    private void handleError(IEventBus iEventBus, Event event, IEventListener[] iEventListeners, int i, Throwable throwable)
    {

    }

    public <T extends NetworkEvent> void addListener(Consumer<T> serverEventListener)
    {
        this.networkEventBus.addListener(serverEventListener);
    }

    public void registerObject(final Object object) {
        this.networkEventBus.register(object);
    }

    public void unregisterObject(final Object object) {
        this.networkEventBus.unregister(object);
    }

    void dispatch(final NetworkSide side, final PacketBuffer bufferData, final NetworkManager manager)
    {
        this.networkEventBus.post(side.getEvent(bufferData,manager));
    }


    public static class NetworkEvent extends Event {
        private final PacketBuffer payload;
        private final NetworkManager source;

        private NetworkEvent(PacketBuffer payload, NetworkManager source)
        {
            this.payload = payload;
            this.source = source;
        }

        public PacketBuffer getPayload()
        {
            return payload;
        }

        public NetworkManager getSource()
        {
            return source;
        }

        public static class ServerCustomPayloadEvent extends NetworkEvent {
            ServerCustomPayloadEvent(final PacketBuffer payload, final NetworkManager source) {
                super(payload, source);
            }
        }
        public static class ClientCustomPayloadEvent extends NetworkEvent {
            ClientCustomPayloadEvent(final PacketBuffer payload, final NetworkManager source) {
                super(payload, source);
            }
        }
    }
}
