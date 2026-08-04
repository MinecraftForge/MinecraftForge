/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission.context;

import com.google.common.base.Preconditions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class PlayerContext extends Context
{
    private final PlayerEntity player;

    public PlayerContext(PlayerEntity ep)
    {
        player = Preconditions.checkNotNull(ep, "Player can't be null in PlayerContext!");
    }

    @Override
    public World getWorld()
    {
        return player.getEntityWorld();
    }

    @Override
    public PlayerEntity getPlayer()
    {
        return player;
    }
}
