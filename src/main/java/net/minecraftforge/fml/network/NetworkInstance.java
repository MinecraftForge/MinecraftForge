/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventListener;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class NetworkInstance
{
    public ResourceLocation getChannelName()
    {
        return channelName;
    }

    private final ResourceLocation channelName;
    private final String networkProtocolVersion;
    private final Predicate<String> clientAcceptedVersions;
    private final Predicate<String> serverAcceptedVersions;
    private final IEventBus networkEventBus;

    NetworkInstance(ResourceLocation channelName, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions)
    {
        this.channelName = channelName;
        this.networkProtocolVersion = networkProtocolVersion.get();
        this.clientAcceptedVersions = clientAcceptedVersions;
        this.serverAcceptedVersions = serverAcceptedVersions;
        this.networkEventBus = IEventBus.create(this::handleError);
    }

    private void handleError(IEventBus iEventBus, Event event, IEventListener[] iEventListeners, int i, Throwable throwable)
    {

    }

    public <T extends NetworkEvent> void addListener(Consumer<T> eventListener)
    {
        this.networkEventBus.addListener(eventListener);
    }

    public void addGatherListener(Consumer<NetworkEvent.GatherLoginPayloadsEvent> eventListener)
    {
        this.networkEventBus.addListener(eventListener);
    }

    public void registerObject(final Object object) {
        this.networkEventBus.register(object);
    }

    public void unregisterObject(final Object object) {
        this.networkEventBus.unregister(object);
    }

    boolean dispatch(final NetworkDirection side, final ICustomPacket<?> packet, final NetworkManager manager, final EntityPlayerMP player)
    {
        final NetworkEvent.Context context = new NetworkEvent.ContextServer(manager, side, packet.getIndex(), player);
        this.networkEventBus.post(side.getEvent(packet, () -> context));
        return context.getPacketHandled();
    }


    String getNetworkProtocolVersion() {
        return networkProtocolVersion;
    }

    boolean tryServerVersionOnClient(final String serverVersion) {
        return this.clientAcceptedVersions.test(serverVersion);
    }

    boolean tryClientVersionOnServer(final String clientVersion) {
        return this.serverAcceptedVersions.test(clientVersion);
    }

    void dispatchGatherLogin(final List<NetworkRegistry.LoginPayload> loginPayloadList) {
        this.networkEventBus.post(new NetworkEvent.GatherLoginPayloadsEvent(loginPayloadList));
    }

    void dispatchLoginPacket(final NetworkEvent.LoginPayloadEvent loginPayloadEvent) {
        this.networkEventBus.post(loginPayloadEvent);
    }
}
