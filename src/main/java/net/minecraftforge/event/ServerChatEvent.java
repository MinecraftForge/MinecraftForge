/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * ServerChatEvent is fired whenever a C01PacketChatMessage is processed. <br>
 * This event is fired via {@link ForgeHooks#onServerChatEvent(ServerGamePacketListenerImpl, String, Component)},
 * which is executed by the {@link ServerGamePacketListenerImpl#handleChat(ServerboundChatPacket)}<br>
 * <br>
 * {@link #username} contains the username of the player sending the chat message.<br>
 * {@link #message} contains the message being sent.<br>
 * {@link #player} the instance of EntityPlayerMP for the player sending the chat message.<br>
 * {@link #component} contains the instance of ChatComponentTranslation for the sent message.<br>
 * <br>
 * This event is {@link Cancelable}. <br>
 * If this event is canceled, the chat message is never distributed to all clients.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class ServerChatEvent extends net.minecraftforge.eventbus.api.Event
{
    private final String message;
    @Nullable
    private final String filteredMessage;
    private final String username;
    private final ServerPlayer player;
    @Nullable
    private Component component;
    @Nullable
    private Component filteredComponent;

    @Deprecated
    public ServerChatEvent(ServerPlayer player, String message, Component component)
    {
        this(player, message, component, null, null);
    }

    public ServerChatEvent(ServerPlayer player, String message, Component component, @Nullable String filteredMessage, @Nullable Component filteredComponent)
    {
        super();
        this.message = message;
        this.filteredMessage = filteredMessage;
        this.player = player;
        this.username = player.getGameProfile().getName();
        this.component = component;
        this.filteredComponent = filteredComponent;
    }

    public void setComponent(Component e) {
        this.component = e;
        if (this.message.equals(filteredMessage))
            setFilteredComponent(e);
    }

    public void setFilteredComponent(Component e) { this.filteredComponent = e; }
    public Component getComponent() { return this.component; }
    public Component getFilteredComponent() { return this.filteredComponent; }
    public String getMessage() { return this.message; }
    public String getFilteredMessage() { return this.filteredMessage; }
    public String getUsername() { return this.username; }
    public ServerPlayer getPlayer() { return this.player; }
}
