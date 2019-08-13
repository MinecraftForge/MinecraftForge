/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.multipart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

/**
 * The default implementation of {@link MultipartHandler} shipped with Forge.<br/>
 * Placement, removal and retrieval logic is the same as vanilla.<br/>
 *
 * This is what will be used if there are no mod-provided implementations, or if the user forces this handler to be used.
 *
 * @see MultipartHandler
 */
public final class NoOpMultipartHandler extends MultipartHandler
{
    public static final MultipartHandler INSTANCE = new NoOpMultipartHandler().setRegistryName("forge", "noop");

    private NoOpMultipartHandler()
    {
    }

    @Override
    public Set<IBlockSlot> getOccupiedSlots(IBlockReader world, BlockPos pos)
    {
        BlockState currentState = world.getBlockState(pos);
        if (currentState.isAir(world, pos))
        {
            return Collections.emptySet();
        }
        return Collections.singleton(currentState.getSlot());
    }

    @Override
    public BlockState getBlockState(IBlockReader world, BlockPos pos, IBlockSlot slot)
    {
        BlockState currentState = world.getBlockState(pos);
        if (slot == BlockSlot.FULL_BLOCK || slot == currentState.getSlot())
        {
            return currentState;
        }
        return Blocks.AIR.getDefaultState();
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(IBlockReader world, BlockPos pos, IBlockSlot slot)
    {
        BlockState currentState = world.getBlockState(pos);
        if (slot == BlockSlot.FULL_BLOCK || slot == currentState.getSlot())
        {
            return world.getTileEntity(pos);
        }
        return null;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(IBlockReader world, BlockPos pos, BlockState state)
    {
        BlockState currentState = world.getBlockState(pos);
        return state == currentState ? world.getTileEntity(pos) : null;
    }

    @Override
    public boolean canAddBlockState(IWorld world, BlockPos pos, BlockState state)
    {
        return world.getBlockState(pos).isAir(world, pos);
    }

    @Override
    public boolean addBlockState(IWorld world, BlockPos pos, BlockState state, int flags)
    {
        BlockState currentState = world.getBlockState(pos);
        return currentState.isAir(world, pos) && world.setBlockState(pos, state, flags);
    }

    @Override
    public boolean replaceBlockState(IWorld world, BlockPos pos, BlockState originalState, BlockState newState, int flags)
    {
        BlockState currentState = world.getBlockState(pos);
        return originalState == currentState && world.setBlockState(pos, newState, flags);
    }

    @Override
    public boolean removeBlockState(IWorld world, BlockPos pos, BlockState state, boolean isMoving)
    {
        BlockState currentState = world.getBlockState(pos);
        return currentState == state && world.removeBlock(pos, isMoving);
    }

    @Override
    public boolean destroyBlockState(IWorld world, BlockPos pos, BlockState state, boolean dropBlock)
    {
        BlockState currentState = world.getBlockState(pos);
        return currentState == state && world.destroyBlock(pos, dropBlock);
    }
}
