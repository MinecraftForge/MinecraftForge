/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * This event is fired when game checks, if sleeping player should be still considered "in bed".<br>
 * Failing this check will cause player to wake up.<br>
 *
 * This event has a result. {@link HasResult}<br>
 *
 * setResult(ALLOW) informs game that player is still "in bed"<br>
 * setResult(DEFAULT) causes game to check {@link Block#isBed(BlockState, BlockGetter, BlockPos, Entity)} instead
 */
@HasResult
public class SleepingLocationCheckEvent extends LivingEvent
{

    private final BlockPos sleepingLocation;

    public SleepingLocationCheckEvent(LivingEntity player, BlockPos sleepingLocation)
    {
        super(player);
        this.sleepingLocation = sleepingLocation;
    }

    public BlockPos getSleepingLocation()
    {
        return sleepingLocation;
    }
}
