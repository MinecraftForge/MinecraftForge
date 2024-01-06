/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission.context;

import com.google.common.base.Preconditions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import javax.annotation.Nullable;

public class BlockPosContext extends PlayerContext
{
    private final BlockPos blockPos;
    private BlockState blockState;
    private Direction facing;

    public BlockPosContext(Player ep, BlockPos pos, @Nullable BlockState state, @Nullable Direction f)
    {
        super(ep);
        blockPos = Preconditions.checkNotNull(pos, "BlockPos can't be null in BlockPosContext!");
        blockState = state;
        facing = f;
    }

    public BlockPosContext(Player ep, ChunkPos pos)
    {
        this(ep, new BlockPos(pos.getMinBlockX() + 8, 0, pos.getMinBlockZ() + 8), null, null);
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
