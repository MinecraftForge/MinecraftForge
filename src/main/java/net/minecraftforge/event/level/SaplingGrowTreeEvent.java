/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * SaplingGrowTreeEvent is fired when a sapling grows into a tree.<br>
 * This event is fired during sapling growth in
 * {@link SaplingBlock#advanceTree(ServerLevel, BlockPos, BlockState, RandomSource)} .<br>
 * <br>
 * {@link #getPos()} contains the coordinates of the growing sapling. <br>
 * {@link #getRand()} contains an instance of {@link RandomSource} for use. <br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event has a result. {@link HasResult} <br>
 * This result determines if the sapling is allowed to grow. <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@HasResult
public class SaplingGrowTreeEvent extends LevelEvent
{
    private final BlockPos pos;
    private final RandomSource rand;

    public SaplingGrowTreeEvent(LevelAccessor level, RandomSource rand, BlockPos pos)
    {
        super(level);
        this.rand = rand;
        this.pos = pos;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public RandomSource getRand()
    {
        return rand;
    }
}
