/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import java.util.Objects;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatPreviewPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired whenever a {@link ServerboundChatPreviewPacket} or {@link ServerboundChatPacket} is received.
 * <p>
 * Mods that modify chat messages from this event should call {@link ForgeMod#enableServerChatPreview()}
 * from their mod constructor to enable the chat preview and allow signing by clients.
 * It is recommended to listen to this event class when modifying chat messages
 * rather than a subclass to ensure clients with {@link Options#onlyShowSecureChat()}
 * enabled can see the modified messages.
 * <p>
 * This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 * If the event is cancelled, the message will not be sent to clients.
 * <p>
 * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#SERVER logical server}.
 *
 * @see ForgeMod#enableServerChatPreview()
 **/
@Cancelable
public class ServerChatEvent extends Event
{
    private final ServerPlayer player;
    private final String username;
    private final String rawText;
    private final boolean canChangeMessage;
    private Component message;

    @ApiStatus.Internal
    protected ServerChatEvent(ServerPlayer player, String rawText, Component message, boolean canChangeMessage)
    {
        this.player = player;
        this.username = player.getGameProfile().getName();
        this.rawText = rawText;
        this.canChangeMessage = canChangeMessage;
        this.message = message;
    }

    /**
     * {@return the player who initiated the chat action}
     */
    public ServerPlayer getPlayer()
    {
        return this.player;
    }

    /**
     * {@return the username of the player who initiated the chat action}
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * {@return the original raw text of the player chat message}
     */
    public String getRawText()
    {
        return this.rawText;
    }

    /**
     * {@return whether the message can be changed or not}.
     * In vanilla, this will be false if a {@link ServerboundChatPacket} is received
     * with signed decorated content already included by the sending player.
     * Players who submit a chat message with chat previews disabled will cause this to be true.
     */
    public boolean canChangeMessage()
    {
        return this.canChangeMessage;
    }

    /**
     * Set the message to be sent to the relevant clients.
     * <p>
     * If {@link #canChangeMessage()} is false, this call will be ignored.
     * <p>
     * It is recommended to listen to {@link ServerChatEvent} when modifying chat messages
     * rather than a subclass to ensure clients with {@link Options#onlyShowSecureChat()}
     * enabled can see the modified messages.
     *
     * @see ForgeMod#enableServerChatPreview()
     */
    public void setMessage(Component message)
    {
        if (!this.canChangeMessage)
            return;

        this.message = Objects.requireNonNull(message);
    }

    /**
     * {@return the message that will be sent to the relevant clients, if the event is not cancelled}
     */
    public Component getMessage()
    {
        return this.message;
    }

    /**
     * This event is fired whenever a {@link ServerboundChatPreviewPacket} is received from a client typing a message.
     * <p>
     * This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * If the event is cancelled, the chat preview sent back to the client will match the original message.
     * <p>
     * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#SERVER logical server}.
     **/
    public static class Preview extends ServerChatEvent
    {
        @ApiStatus.Internal
        public Preview(ServerPlayer player, String rawText, Component message)
        {
            super(player, rawText, message, true);
        }

        /**
         * Set the chat preview to be sent back to the client.
         */
        @Override
        public void setMessage(Component message)
        {
            super.setMessage(message);
        }

        /**
         * {@return the chat preview that will be sent back to the client, if the event is not cancelled}
         */
        @Override
        public Component getMessage()
        {
            return super.getMessage();
        }
    }

    /**
     * This event is fired whenever a {@link ServerboundChatPacket} is received from a client
     * who has submitted their chat message.
     * <p>
     * This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * If the event is cancelled, the message will not be sent to clients.
     * <p>
     * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#SERVER logical server}.
     **/
    public static class Submitted extends ServerChatEvent
    {
        @ApiStatus.Internal
        public Submitted(ServerPlayer player, String rawText, Component message, boolean canChangeMessage)
        {
            super(player, rawText, message, canChangeMessage);
        }
    }
}
