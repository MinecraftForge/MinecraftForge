/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired when the ender dragon or wither attempts to destroy a block and when ever a zombie attempts to break a door. Basically a event version of {@link Block#canEntityDestroy(IBlockState, IBlockAccess, BlockPos, Entity)}<br>
 * <br>
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * If this event is canceled, the block will not be destroyed.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class LivingDestroyBlockEvent extends LivingEvent
{
    private final BlockPos pos;
    private final BlockState state;
    
    public LivingDestroyBlockEvent(LivingEntity entity, BlockPos pos, BlockState state)
    {
        super(entity);
        this.pos = pos;
        this.state = state;
    }

    public BlockState getState()
    {
        return state;
    }
    
    public BlockPos getPos()
    {
        return pos;
    }
}
