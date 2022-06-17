/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

/**
 * ServerChatEvent is fired whenever a C01PacketChatMessage is processed. <br>
 * This event is fired via {@link ForgeHooks#onServerChatEvent(ServerPlayer, String, Component)},
 * which is executed by the {@link ServerGamePacketListenerImpl#handleChat(ServerboundChatPacket)}<br>
 * <br>
 * {@link #username} contains the username of the player sending the chat message, unless fired from the server, where it is null.<br>
 * {@link #message} contains the message being sent.<br>
 * {@link #player} the instance of ServerPlayer for the player sending the chat message, or null when fired from the server.<br>
 * {@link #component} contains the instance of Component for the sent message.<br>
 * <br>
 * This event is {@link Cancelable}. <br>
 * If this event is canceled, the chat message is never distributed to all clients.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class ServerChatEvent extends Event
{
    private final String message;
    @Nullable
    private final String username;
    @Nullable
    private final ServerPlayer player;
    private Component component;

    public ServerChatEvent(@Nullable ServerPlayer player, String message, Component component)
    {
        super();
        this.message = message;
        this.player = player;
        this.username = player != null ? player.getGameProfile().getName() : null;
        this.component = component;
    }

    public void setComponent(Component e)
    {
        this.component = e;
    }

    public Component getComponent()
    {
        return this.component;
    }

    public String getMessage() { return this.message; }
    public @Nullable String getUsername() { return this.username; }
    public @Nullable ServerPlayer getPlayer() { return this.player; }
}
