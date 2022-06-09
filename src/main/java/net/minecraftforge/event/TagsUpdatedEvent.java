/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Fired when tags are updated on either server or client. This event can be used to refresh data that depends on tags.
 */
public class TagsUpdatedEvent extends Event
{
    private final RegistryAccess registries;
    private final UpdateCause updateCause;
    private final boolean integratedServer;

    @Deprecated(forRemoval = true, since = "1.18.2")
    public TagsUpdatedEvent(RegistryAccess registries)
    {
        this.registries = registries;
        this.updateCause = ClientGamePacketListener.class.isAssignableFrom(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass()) ? UpdateCause.CLIENT_PACKET_RECEIVED : UpdateCause.SERVER_DATA_LOAD;
        var currentServer = ServerLifecycleHooks.getCurrentServer();
        this.integratedServer = currentServer != null && !currentServer.isDedicatedServer();
    }

    public TagsUpdatedEvent(RegistryAccess registries, boolean fromClientPacket, boolean isIntegratedServerConnection)
    {
        this.registries = registries;
        this.updateCause = fromClientPacket ? UpdateCause.CLIENT_PACKET_RECEIVED : UpdateCause.SERVER_DATA_LOAD;
        this.integratedServer = isIntegratedServerConnection;
    }

    /**
     * @return The dynamic registries that have had their tags rebound.
     */
    public RegistryAccess getTagManager()
    {
        return registries;
    }

    /**
     * @return the cause for this tag update
     */
    public UpdateCause getUpdateCause()
    {
        return updateCause;
    }

    /**
     * Whether static data (which in single player is shared between server and client thread) should be updated as a
     * result of this event. Effectively this means that in single player only the server-side updates this data.
     */
    public boolean shouldUpdateStaticData()
    {
        return updateCause == UpdateCause.SERVER_DATA_LOAD || !integratedServer;
    }

    /**
     * Represents the cause for a tag update.
     */
    public enum UpdateCause
    {
        /**
         * The tag update is caused by the server loading datapack data. Note that in single player this still happens
         * on the client thread.
         */
        SERVER_DATA_LOAD,
        /**
         * The tag update is caused by the client receiving the tag data from the server.
         */
        CLIENT_PACKET_RECEIVED
    }
}
