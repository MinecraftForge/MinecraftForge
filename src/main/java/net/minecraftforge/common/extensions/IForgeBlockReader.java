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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.multipart.IBlockSlot;
import net.minecraftforge.common.multipart.MultipartManager;

import javax.annotation.Nullable;
import java.util.Set;

public interface IForgeBlockReader
{
    default IBlockReader getBlockReader()
    {
        return (IBlockReader)this;
    }

    /**
     * Gets all the {@link IBlockSlot}s occupied by blocks in the given block space.<br/>
     *
     * If the current state is air, returns an empty set.
     */
    default Set<IBlockSlot> getOccupiedSlots(BlockPos pos)
    {
        return MultipartManager.INSTANCE.getHandler().getOccupiedSlots(getBlockReader(), pos);
    }

    /**
     * Gets the {@link BlockState} at the specified slot in a block.<br/>
     *
     * If there isn't a part in the given slot, air is returned.
     */
    default BlockState getBlockState(BlockPos pos, IBlockSlot slot)
    {
        return MultipartManager.INSTANCE.getHandler().getBlockState(getBlockReader(), pos, slot);
    }

    /**
     * Gets the {@link TileEntity} at the specified slot in a block.<br/>
     *
     * If the slot matches that of a part in this block space, returns that TileEntity, if present.<br/>
     * If there isn't a part in the given slot, returns {@code null}.
     */
    @Nullable
    default TileEntity getTileEntity(BlockPos pos, IBlockSlot slot)
    {
        return MultipartManager.INSTANCE.getHandler().getTileEntity(getBlockReader(), pos, slot);
    }

    /**
     * Gets the {@link TileEntity} for the specified state in a block.<br/>
     *
     * If the state matches that of the block or a part in the block space, returns that TileEntity, if present.
     * If the state does not match anything in the block, returns {@code null}.
     */
    @Nullable
    default TileEntity getTileEntity(BlockPos pos, BlockState state)
    {
        return MultipartManager.INSTANCE.getHandler().getTileEntity(getBlockReader(), pos, state);
    }
}
