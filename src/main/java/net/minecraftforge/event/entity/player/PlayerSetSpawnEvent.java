/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

@net.minecraftforge.eventbus.api.Cancelable
public class PlayerSetSpawnEvent extends PlayerEvent
{
    private final boolean forced;
    private final BlockPos newSpawn;
    
    public PlayerSetSpawnEvent(PlayerEntity player, BlockPos newSpawn, boolean forced) {
        super(player);
        this.newSpawn = newSpawn;
        this.forced = forced;
    }

    /**
     * This event is called before a player's spawn point is changed.
     * The event can be canceled, and no further processing will be done.
     */
    public boolean isForced()
    {
        return forced;
    }

    public BlockPos getNewSpawn()
    {
        return newSpawn;
    }
}
