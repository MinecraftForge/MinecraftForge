/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.MessageEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired whenever a player joins the server. This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 * If the event is cancelled, the message will not be sent to clients.
 * <p>
 * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#SERVER logical server}.
 * <p>
 */
@Cancelable
public class PlayerJoinMessageEvent extends MessageEvent
{

    private final @Nullable String oldName;

    public PlayerJoinMessageEvent(ServerPlayer player, Component message, @Nullable String oldName)
    {
        super(player, message);
        this.oldName = oldName;
    }

    /**
     * @return The old name of the player, if they have changed their name, or null if they have not.
     * Use {@link Player#getDisplayName()} for the current name of the player.
     */
    public @Nullable String getOldName()
    {
        return oldName;
    }

    /**
     * @return True if the player has changed their name, false otherwise.
     */
    public boolean hasPlayerChangedName()
    {
        return oldName != null;
    }
}
