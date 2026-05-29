/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.player;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftsingularity.common.util.HasResult;
import net.minecraftsingularity.common.util.Result;
import net.minecraftsingularity.event.entity.living.LivingEvent;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;

/**
 * This event is fired when game checks, if sleeping player should be still considered "in bed".<br>
 * Failing this check will cause player to wake up.<br>
 *
 * <p>This event {@linkplain HasResult has a result}:</p>
 * <ul>
 *     <li>{@link Result#ALLOW}: informs game that player is still "in bed"</li>
 *     <li>{@link Result#DEFAULT}: causes game to check {@link Block#isBed(BlockState, BlockGetter, BlockPos, Entity)} instead</li>
 * </ul>
 */
public final class SleepingLocationCheckEvent extends MutableEvent implements LivingEvent, HasResult {
    public static final EventBus<SleepingLocationCheckEvent> BUS = EventBus.create(SleepingLocationCheckEvent.class);

    private final LivingEntity entity;
    private final BlockPos sleepingLocation;
    private Result result = Result.DEFAULT;

    public SleepingLocationCheckEvent(LivingEntity player, BlockPos sleepingLocation) {
        this.entity = player;
        this.sleepingLocation = sleepingLocation;
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    public BlockPos getSleepingLocation() {
        return sleepingLocation;
    }

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public void setResult(Result result) {
        this.result = result;
    }
}
