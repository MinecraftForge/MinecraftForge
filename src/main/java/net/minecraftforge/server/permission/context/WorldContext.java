/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission.context;

import com.google.common.base.Preconditions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WorldContext extends Context
{
    private final World world;

    public WorldContext(World w)
    {
        world = Preconditions.checkNotNull(w, "World can't be null in WorldContext!");
    }

    @Override
    public World getWorld()
    {
        return world;
    }

    @Override
    @Nullable
    public PlayerEntity getPlayer()
    {
        return null;
    }
}
