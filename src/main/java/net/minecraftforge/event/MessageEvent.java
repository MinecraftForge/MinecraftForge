/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

public abstract class MessageEvent extends Event
{

    private final ServerPlayer player;
    private Component message;

    @ApiStatus.Internal
    protected MessageEvent(ServerPlayer player, Component message)
    {
        this.player = player;
        this.message = message;
    }

    /**
     * {@return the player who initiated the chat action}
     */
    public ServerPlayer getPlayer()
    {
        return player;
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
        return message;
    }
}
