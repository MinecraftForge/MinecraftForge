/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission.context;

import com.google.common.base.Preconditions;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nullable;

public class BlockPosContext extends PlayerContext
{
    private final BlockPos blockPos;
    private BlockState blockState;
    private Direction facing;

    public BlockPosContext(PlayerEntity ep, BlockPos pos, @Nullable BlockState state, @Nullable Direction f)
    {
        super(ep);
        blockPos = Preconditions.checkNotNull(pos, "BlockPos can't be null in BlockPosContext!");
        blockState = state;
        facing = f;
    }

    public BlockPosContext(PlayerEntity ep, ChunkPos pos)
    {
        this(ep, new BlockPos(pos.getXStart() + 8, 0, pos.getZStart() + 8), null, null);
    }

    @Override
    @Nullable
    public <T> T get(ContextKey<T> key)
    {
        if(key.equals(ContextKeys.POS))
        {
            return (T) blockPos;
        }
        else if(key.equals(ContextKeys.BLOCK_STATE))
        {
            if(blockState == null)
            {
                blockState = getWorld().getBlockState(blockPos);
            }

            return (T) blockState;
        }
        else if(key.equals(ContextKeys.FACING))
        {
            return (T) facing;
        }

        return super.get(key);
    }

    @Override
    protected boolean covers(ContextKey<?> key)
    {
        return key.equals(ContextKeys.POS) || key.equals(ContextKeys.BLOCK_STATE) || (facing != null && key.equals(ContextKeys.FACING));
    }
}
