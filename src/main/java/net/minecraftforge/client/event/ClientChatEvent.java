/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import com.google.common.base.Strings;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Fired when the client is about to send a chat message to the server.
 *
 * <p>This event is {@linkplain Cancellable cancellable}.
 * If the event is cancelled, the chat message will not be sent to the server.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 **/
@NullMarked
public final class ClientChatEvent extends MutableEvent implements Cancellable {
    public static final CancellableEventBus<ClientChatEvent> BUS = CancellableEventBus.create(ClientChatEvent.class);

    private String message;
    private final String originalMessage;

    @ApiStatus.Internal
    public ClientChatEvent(String message) {
        this.setMessage(message);
        this.originalMessage = Strings.nullToEmpty(message);
        this.message = this.originalMessage;
    }

    /**
     * {@return the message that will be sent to the server, if the event is not cancelled. This can be changed by mods}
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Sets the new message to be sent to the server, if the event is not cancelled.
     *
     * @param message the new message to be sent
     */
    public void setMessage(@Nullable String message) {
        this.message = Strings.nullToEmpty(message);
    }

    /**
     * {@return the original message that was to be sent to the server. This cannot be changed by mods}
     */
    public String getOriginalMessage() {
        return this.originalMessage;
    }
}
