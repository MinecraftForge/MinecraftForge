/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

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
@Cancelable
public class ServerChatEvent extends Event
{
    private final ServerPlayer player;
    private final String username;
    private final String rawText;
    private Component message;

    @ApiStatus.Internal
    public ServerChatEvent(ServerPlayer player, String rawText, Component message)
    {
        this.player = player;
        this.username = player.getGameProfile().getName();
        this.rawText = rawText;
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
     * Set the message to be sent to the relevant clients.
     */
    public void setMessage(Component message)
    {
        this.message = Objects.requireNonNull(message);
    }

    /**
     * {@return the message that will be sent to the relevant clients, if the event is not cancelled}
     */
    public Component getMessage()
    {
        return this.message;
    }
}
