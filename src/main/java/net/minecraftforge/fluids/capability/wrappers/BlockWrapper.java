/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fluids.capability.wrappers;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.VoidFluidHandler;

/**
 * Wrapper around any block, only accounts for fluid placement, otherwise the block acts a void.
 * If the block in question inherits from the default Vanilla or Forge implementations,
 * consider using {@link BlockLiquidWrapper} or {@link FluidBlockWrapper} respectively.
 */
public class BlockWrapper extends VoidFluidHandler
{
    protected final Block block;
    protected final World world;
    protected final BlockPos blockPos;

    public BlockWrapper(Block block, World world, BlockPos blockPos)
    {
        this.block = block;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        // NOTE: "Filling" means placement in this context!
        if (resource.amount < Fluid.BUCKET_VOLUME)
        {
            return 0;
        }
        if (doFill)
        {
            FluidUtil.destroyBlockOnFluidPlacement(world, blockPos);
            world.setBlockState(blockPos, block.getDefaultState(), 11);
        }
        return Fluid.BUCKET_VOLUME;
    }
}