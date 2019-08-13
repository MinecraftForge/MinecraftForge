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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.extensions.IForgeBlockState;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Exposes the methods needed for placement, removal and retrieval of parts within a single block space.<br/>
 * Implementations <b>must</b> respect vanilla behavior for blocks they cannot handle.<br/>
 *
 * Forge provides a default implementation that replicates vanilla behavior.
 *
 * @see NoOpMultipartHandler
 */
public abstract class MultipartHandler extends ForgeRegistryEntry<MultipartHandler>
{
    /**
     * Gets all the {@link IBlockSlot}s occupied by blocks in the given block space.<br/>
     *
     * If the current state is air, this method must return an empty set.<br/>
     * If the given block space contains a single block, this method must return a set with its state's slot as its sole element.<br/>
     * If the given block space contains multiple blocks, this method must return a set of all the slots they take up.
     */
    public abstract Set<IBlockSlot> getOccupiedSlots(IBlockReader world, BlockPos pos);

    /**
     * Gets the {@link BlockState} at the specified slot in a block.<br/>
     *
     * If the slot is {@link BlockSlot#FULL_BLOCK}, this method must return {@code world.getBlockState(pos)}.<br/>
     * If the slot matches that of a part in this block space, this method must return that BlockState.<br/>
     * If there isn't a part in the given slot, the returned state must return {@code true} when {@link BlockState#isAir(IBlockReader, BlockPos)} is called.
     */
    public abstract BlockState getBlockState(IBlockReader world, BlockPos pos, IBlockSlot slot);

    /**
     * Gets the {@link TileEntity} at the specified slot in a block.<br/>
     *
     * If the slot is {@link BlockSlot#FULL_BLOCK}, this method must return {@code world.getTileEntity(pos)}.<br/>
     * If the slot matches that of a part in this block space, this method must return that TileEntity, if present.<br/>
     * If there isn't a part in the given slot, this method must return {@code null}.
     */
    @Nullable
    public abstract TileEntity getTileEntity(IBlockReader world, BlockPos pos, IBlockSlot slot);

    /**
     * Gets the {@link TileEntity} for the specified state in a block.<br/>
     *
     * If the state matches that of the block (as returned by {@code world.getBlockState(pos)}), this method must return
     * {@code world.getTileEntity(pos)}.<br/>
     * If the state matches that of a part in this block space, this method must return that TileEntity, if present.<br/>
     * If the state does not match either of the above cases, this method must return {@code null}.
     */
    @Nullable
    public abstract TileEntity getTileEntity(IBlockReader world, BlockPos pos, BlockState state);

    /**
     * Checks if the given {@link BlockState} can be added to the world at the given position.<br/>
     *
     * If the current state at that position returns true in {@link BlockState#isAir(IBlockReader, BlockPos)}, this method
     * must return {@code true}.<br/>
     * If a block is already present, behavior will depend on the specific implementation, but should generally check for
     * multipart compatibility (see {@link IForgeBlockState#getSlot()}) and perform an occlusion test.
     */
    public abstract boolean canAddBlockState(IWorld world, BlockPos pos, BlockState state);

    /**
     * Attempts to add the given {@link BlockState} to the world at the given position.<br/>
     *
     * If the current state at that position returns true in {@link BlockState#isAir(IBlockReader, BlockPos)}, this method
     * can directly set the state at that position, ignoring any multipart behavior.<br/>
     *
     * {@link #canAddBlockState(IWorld, BlockPos, BlockState)} is assumed to have been called before this method.
     */
    public abstract boolean addBlockState(IWorld world, BlockPos pos, BlockState state, int flags);

    /**
     * Replaces the given {@link BlockState} with another.<br/>
     *
     * If the original state matches that of the block (as returned by {@code world.getBlockState(pos)}), this method
     * must replace the state by calling {@code world.getBlockState(pos)} directly and return its result.<br/>
     * If the original state matches that of a part in this block space, that part is to be replaced.<br/>
     * If the original state does not match either of the above cases, this method must return {@code false}.
     */
    public abstract boolean replaceBlockState(IWorld world, BlockPos pos, BlockState originalState, BlockState newState, int flags);

    /**
     * Removes the given {@link BlockState} from the world.<br/>
     *
     * If the state matches that of the block (as returned by {@code world.getBlockState(pos)}), this method must call
     * {@code world.removeBlock(pos, isMoving)} directly and return its result.<br/>
     * If the state matches that of a part in this block space, that part is to be removed.<br/>
     * If the state does not match either of the above cases, this method must return {@code false}.
     */
    public abstract boolean removeBlockState(IWorld world, BlockPos pos, BlockState state, boolean isMoving);

    /**
     * Destroys the given {@link BlockState}.<br/>
     *
     * If the state matches that of the block (as returned by {@code world.getBlockState(pos)}), this method must call
     * {@code world.destroyBlock(pos, dropBlock)} directly and return its result.<br/>
     * If the state matches that of a part in this block space, that part is to be destroyed.<br/>
     * If the state does not match either of the above cases, this method must return {@code false}.
     */
    public abstract boolean destroyBlockState(IWorld world, BlockPos pos, BlockState state, boolean dropBlock);
}
