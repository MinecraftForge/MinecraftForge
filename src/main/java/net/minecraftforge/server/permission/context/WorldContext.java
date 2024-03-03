/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission.context;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class WorldContext extends Context
{
    private final Level world;

    public WorldContext(Level w)
    {
        world = Preconditions.checkNotNull(w, "World can't be null in WorldContext!");
    }

    @Override
    public Level getWorld()
    {
        return world;
    }

    @Override
    @Nullable
    public Player getPlayer()
    {
        return null;
    }
}
