/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
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
public class ServerChatEvent extends MessageEvent
{
    private final String username;
    private final String rawText;

    @ApiStatus.Internal
    public ServerChatEvent(ServerPlayer player, String rawText, Component message)
    {
        super(player, message);
        this.username = player.getGameProfile().getName();
        this.rawText = rawText;
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
}
