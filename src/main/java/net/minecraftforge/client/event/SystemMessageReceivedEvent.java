/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when a system message is received on the client.
 * This can be used for filtering and detecting messages with specific words or phrases, and suppressing them.
 *
 * <p>If the event is cancelled, the message is not displayed in the chat message window.</p>
 *
 */
@Cancelable
public class SystemMessageReceivedEvent extends Event {
    private Component message;
    private final boolean overlay;

    @ApiStatus.Internal
    public SystemMessageReceivedEvent(Component message, boolean overlay) {
        this.message = message;
        this.overlay = overlay;
    }

    /**
     * {@return whether the message goes to the overlay}
     */
    public boolean isOverlay() {
        return this.overlay;
    }

    /**
     * {@return the message that will be displayed in the chat message window, if the event is not cancelled}
     */
    public Component getMessage() {
        return message;
    }

    /**
     * Sets the new message to be displayed in the chat message window, if the event is not cancelled.
     *
     * @param message the new message to be displayed
     */
    public void setMessage(Component message) {
        this.message = message;
    }
}