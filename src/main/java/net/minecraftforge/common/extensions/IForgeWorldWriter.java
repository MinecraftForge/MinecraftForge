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

package net.minecraftforge.common.extensions;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.multipart.MultipartManager;

import javax.annotation.Nullable;

public interface IForgeWorldWriter
{
    @Nullable
    default IWorld getWorldWriterAsIWorld()
    {
        return this instanceof IWorld ? (IWorld) this : null;
    }

    /**
     * Checks if the given {@link BlockState} can be added to the world at the given position.
     */
    default boolean canAddBlockState(BlockPos pos, BlockState state)
    {
        IWorld world = getWorldWriterAsIWorld();
        if (world == null) return false;
        return MultipartManager.INSTANCE.getHandler().canAddBlockState(world, pos, state);
    }

    /**
     * Attempts to add the given {@link BlockState} to the world at the given position.
     */
    default boolean addBlockState(BlockPos pos, BlockState state)
    {
        return addBlockState(pos, state, 3);
    }

    /**
     * Attempts to add the given {@link BlockState} to the world at the given position.
     */
    default boolean addBlockState(BlockPos pos, BlockState state, int flags)
    {
        IWorld world = getWorldWriterAsIWorld();
        if (world == null) return false;
        return MultipartManager.INSTANCE.getHandler().addBlockState(world, pos, state, flags);
    }

    /**
     * Replaces the given {@link BlockState}, if present, with another.
     */
    default boolean replaceBlockState(BlockPos pos, BlockState originalState, BlockState newState)
    {
        return replaceBlockState(pos, originalState, newState, 3);
    }

    /**
     * Replaces the given {@link BlockState}, if present, with another.
     */
    default boolean replaceBlockState(BlockPos pos, BlockState originalState, BlockState newState, int flags)
    {
        IWorld world = getWorldWriterAsIWorld();
        if (world == null) return false;
        return MultipartManager.INSTANCE.getHandler().replaceBlockState(world, pos, originalState, newState, flags);
    }

    /**
     * Removes the given {@link BlockState} from the world, if present.
     */
    default boolean removeBlockState(BlockPos pos, BlockState state, boolean isMoving)
    {
        IWorld world = getWorldWriterAsIWorld();
        if (world == null) return false;
        return MultipartManager.INSTANCE.getHandler().removeBlockState(world, pos, state, isMoving);
    }

    /**
     * Destroys the given {@link BlockState}, if present.
     */
    default boolean destroyBlockState(BlockPos pos, BlockState state, boolean dropBlock)
    {
        IWorld world = getWorldWriterAsIWorld();
        if (world == null) return false;
        return MultipartManager.INSTANCE.getHandler().destroyBlockState(world, pos, state, dropBlock);
    }
}
