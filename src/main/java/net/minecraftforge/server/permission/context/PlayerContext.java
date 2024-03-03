/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission.context;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PlayerContext extends Context
{
    private final Player player;

    public PlayerContext(Player ep)
    {
        player = Preconditions.checkNotNull(ep, "Player can't be null in PlayerContext!");
    }

    @Override
    public Level getWorld()
    {
        return player.getCommandSenderWorld();
    }

    @Override
    public Player getPlayer()
    {
        return player;
    }
}
