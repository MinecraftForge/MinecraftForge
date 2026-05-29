/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.InheritableEvent;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import java.util.UUID;

/**
 * Fired when a chat message is received on the client.
 * This can be used for filtering and detecting messages with specific words or phrases, and suppressing them.
 *
 * <p>This event is {@linkplain Cancellable cancellable}.
 * If the event is cancelled, the message is not displayed in the chat message window.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see ChatType
 */
public sealed class ClientChatReceivedEvent extends MutableEvent implements Cancellable, InheritableEvent {
    public static final CancellableEventBus<ClientChatReceivedEvent> BUS = CancellableEventBus.create(ClientChatReceivedEvent.class);

    private Component message;
    private final ChatType.Bound boundChatType;
    private final UUID sender;

    @ApiStatus.Internal
    public ClientChatReceivedEvent(ChatType.Bound boundChatType, Component message, UUID sender) {
        this.boundChatType = boundChatType;
        this.message = message;
        this.sender = sender;
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

    /**
     * {@return the bound chat type of the chat message}.
     * This contains the chat type, display name of the sender, and nullable target name depending on the chat type.
     */
    public ChatType.Bound getBoundChatType() {
        return this.boundChatType;
    }

    /**
     * {@return the message sender}.
     */
    public UUID getSender() {
        return this.sender;
    }

    /**
     * Fired when a player chat message is received on the client.
     *
     * <p>This event is {@linkplain Cancellable cancellable}.
     * If the event is cancelled, the message is not displayed in the chat message window.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @see ChatType
     */
    public static final class Player extends ClientChatReceivedEvent {
        public static final CancellableEventBus<Player> BUS = CancellableEventBus.create(Player.class);

        private final PlayerChatMessage playerChatMessage;

        @ApiStatus.Internal
        public Player(ChatType.Bound boundChatType, Component message, PlayerChatMessage playerChatMessage, UUID sender) {
            super(boundChatType, message, sender);
            this.playerChatMessage = playerChatMessage;
        }

        /**
         * {@return the full player chat message}.
         * This contains the sender UUID, various signing data, and the optional unsigned contents.
         */
        public PlayerChatMessage getPlayerChatMessage() {
            return this.playerChatMessage;
        }
    }
}
