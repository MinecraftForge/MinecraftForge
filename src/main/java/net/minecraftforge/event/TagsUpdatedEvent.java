/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event;

import net.minecraft.core.RegistryAccess;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;

/**
 * Fired when tags are updated on either server or client. This event can be used to refresh data that depends on tags.
 *
 * @param getRegistryAccess the dynamic registries that have had their tags rebound
 * @param getUpdateCause the cause for this tag update
 * @param isIntegratedServer whether this event is being fired on an integrated server
 */
public record TagsUpdatedEvent(RegistryAccess getRegistryAccess, UpdateCause getUpdateCause, boolean isIntegratedServer)
        implements RecordEvent {
    public static final EventBus<TagsUpdatedEvent> BUS = EventBus.create(TagsUpdatedEvent.class);

    public TagsUpdatedEvent(RegistryAccess registryAccess, boolean fromClientPacket, boolean isIntegratedServerConnection) {
        this(registryAccess, fromClientPacket ? UpdateCause.CLIENT_PACKET_RECEIVED : UpdateCause.SERVER_DATA_LOAD, isIntegratedServerConnection);
    }

    /**
     * Whether static data (which in single player is shared between server and client thread) should be updated as a
     * result of this event. Effectively this means that in single player only the server-side updates this data.
     */
    public boolean shouldUpdateStaticData() {
        return getUpdateCause == UpdateCause.SERVER_DATA_LOAD || !isIntegratedServer;
    }

    /**
     * Represents the cause for a tag update.
     */
    public enum UpdateCause {
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
