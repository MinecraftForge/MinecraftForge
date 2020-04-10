/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fluids;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.common.primitives.Ints;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is a fluid block implementation which emulates vanilla Minecraft fluid behavior.
 *
 * It is highly recommended that you use/extend this class for "classic" fluid blocks.
 *
 */
public class BlockFluidClassic extends BlockFluidBase
{
    protected static final List<EnumFacing> SIDES = Collections.unmodifiableList(Arrays.asList(
            EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH));

    protected boolean[] isOptimalFlowDirection = new boolean[4];
    protected int[] flowCost = new int[4];

    protected boolean canCreateSources = false;

    protected FluidStack stack;

    public BlockFluidClassic(Fluid fluid, Material material, MapColor mapColor)
    {
        super(fluid, material, mapColor);
        stack = new FluidStack(fluid, Fluid.BUCKET_VOLUME);
    }

    public BlockFluidClassic(Fluid fluid, Material material)
    {
        this(fluid, material, material.getMaterialMapColor());
    }

    public BlockFluidClassic setFluidStack(FluidStack stack)
    {
        this.stack = stack;
        return this;
    }

    public BlockFluidClassic setFluidStackAmount(int amount)
    {
        this.stack.amount = amount;
        return this;
    }

    @Override
    public int getQuantaValue(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock().isAir(state, world, pos))
        {
            return 0;
        }

        if (state.getBlock() != this)
        {
            return -1;
        }

        return quantaPerBlock - state.getValue(LEVEL);
    }

    @Override
    public boolean canCollideCheck(@Nonnull IBlockState state, boolean fullHit)
    {
        return fullHit && state.getValue(LEVEL) == 0;
    }

    @Override
    public int getMaxRenderHeightMeta()
    {
        return 0;
    }

    @Override
    public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand)
    {
        int quantaRemaining = quantaPerBlock - state.getValue(LEVEL);
        int expQuanta = -101;

        // check adjacent block levels if non-source
        if (quantaRemaining < quantaPerBlock)
        {
            int adjacentSourceBlocks = 0;

            if (ForgeEventFactory.canCreateFluidSource(world, pos, state, canCreateSources))
            {
                for (EnumFacing side : EnumFacing.Plane.HORIZONTAL)
                {
                    if (isSourceBlock(world, pos.offset(side))) adjacentSourceBlocks++;
                }
            }

            // new source block
            if (adjacentSourceBlocks >= 2 && (world.getBlockState(pos.up(densityDir)).getMaterial().isSolid() || isSourceBlock(world, pos.up(densityDir))))
            {
                expQuanta = quantaPerBlock;
            }
            // vertical flow into block
            else if (hasVerticalFlow(world, pos))
            {
                expQuanta = quantaPerBlock - 1;
            }
            else
            {
                int maxQuanta = -100;
                for (EnumFacing side : EnumFacing.Plane.HORIZONTAL)
                {
                    maxQuanta = getLargerQuanta(world, pos.offset(side), maxQuanta);
                }
                expQuanta = maxQuanta - 1;
            }

            // decay calculation
            if (expQuanta != quantaRemaining)
            {
                quantaRemaining = expQuanta;

                if (expQuanta <= 0)
                {
                    world.setBlockToAir(pos);
                }
                else
                {
                    world.setBlockState(pos, state.withProperty(LEVEL, quantaPerBlock - expQuanta), Constants.BlockFlags.SEND_TO_CLIENTS);
                    world.scheduleUpdate(pos, this, tickRate);
                    world.notifyNeighborsOfStateChange(pos, this, false);
                }
            }
        }

        // Flow vertically if possible
        if (canDisplace(world, pos.up(densityDir)))
        {
            flowIntoBlock(world, pos.up(densityDir), 1);
            return;
        }

        // Flow outward if possible
        int flowMeta = quantaPerBlock - quantaRemaining + 1;
        if (flowMeta >= quantaPerBlock)
        {
            return;
        }

        if (isSourceBlock(world, pos) || !isFlowingVertically(world, pos))
        {
            if (hasVerticalFlow(world, pos))
            {
                flowMeta = 1;
            }
            boolean flowTo[] = getOptimalFlowDirections(world, pos);
            for (int i = 0; i < 4; i++)
            {
                if (flowTo[i]) flowIntoBlock(world, pos.offset(SIDES.get(i)), flowMeta);
            }
        }
    }

    protected final boolean hasDownhillFlow(IBlockAccess world, BlockPos pos, EnumFacing direction)
    {
        return world.getBlockState(pos.offset(direction).down(densityDir)).getBlock() == this
                && (canFlowInto(world, pos.offset(direction))
                ||  canFlowInto(world, pos.down(densityDir)));
    }

    public boolean isFlowingVertically(IBlockAccess world, BlockPos pos)
    {
        return world.getBlockState(pos.up(densityDir)).getBlock() == this ||
            (world.getBlockState(pos).getBlock() == this && canFlowInto(world, pos.up(densityDir)));
    }

    public boolean isSourceBlock(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == this && state.getValue(LEVEL) == 0;
    }

    protected boolean[] getOptimalFlowDirections(World world, BlockPos pos)
    {
        for (int side = 0; side < 4; side++)
        {
            flowCost[side] = 1000;

            BlockPos pos2 = pos.offset(SIDES.get(side));

            if (!canFlowInto(world, pos2) || isSourceBlock(world, pos2))
            {
                continue;
            }

            if (canFlowInto(world, pos2.up(densityDir)))
            {
                flowCost[side] = 0;
            }
            else
            {
                flowCost[side] = calculateFlowCost(world, pos2, 1, side);
            }
        }

        int min = Ints.min(flowCost);
        for (int side = 0; side < 4; side++)
        {
            isOptimalFlowDirection[side] = flowCost[side] == min;
        }
        return isOptimalFlowDirection;
    }

    protected int calculateFlowCost(World world, BlockPos pos, int recurseDepth, int side)
    {
        int cost = 1000;
        for (int adjSide = 0; adjSide < 4; adjSide++)
        {
            if (SIDES.get(adjSide) == SIDES.get(side).getOpposite())
            {
                continue;
            }

            BlockPos pos2 = pos.offset(SIDES.get(adjSide));

            if (!canFlowInto(world, pos2) || isSourceBlock(world, pos2))
            {
                continue;
            }

            if (canFlowInto(world, pos2.up(densityDir)))
            {
                return recurseDepth;
            }

            if (recurseDepth >= quantaPerBlock / 2)
            {
                continue;
            }

            cost = Math.min(cost, calculateFlowCost(world, pos2, recurseDepth + 1, adjSide));
        }
        return cost;
    }

    protected void flowIntoBlock(World world, BlockPos pos, int meta)
    {
        if (meta < 0) return;
        if (displaceIfPossible(world, pos))
        {
            world.setBlockState(pos, this.getDefaultState().withProperty(LEVEL, meta));
        }
    }

    protected boolean canFlowInto(IBlockAccess world, BlockPos pos)
    {
        return world.getBlockState(pos).getBlock() == this || canDisplace(world, pos);
    }

    protected int getLargerQuanta(IBlockAccess world, BlockPos pos, int compare)
    {
        int quantaRemaining = getEffectiveQuanta(world, pos);
        if (quantaRemaining <= 0)
        {
            return compare;
        }
        return quantaRemaining >= compare ? quantaRemaining : compare;
    }

    /* IFluidBlock */
    @Override
    public int place(World world, BlockPos pos, @Nonnull FluidStack fluidStack, boolean doPlace)
    {
        if (fluidStack.amount < Fluid.BUCKET_VOLUME)
        {
            return 0;
        }
        if (doPlace)
        {
            FluidUtil.destroyBlockOnFluidPlacement(world, pos);
            world.setBlockState(pos, this.getDefaultState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
        }
        return Fluid.BUCKET_VOLUME;
    }

    @Override
    @Nullable
    public FluidStack drain(World world, BlockPos pos, boolean doDrain)
    {
        if (!isSourceBlock(world, pos))
        {
            return null;
        }

        if (doDrain)
        {
            world.setBlockToAir(pos);
        }

        return stack.copy();
    }

    @Override
    public boolean canDrain(World world, BlockPos pos)
    {
        return isSourceBlock(world, pos);
    }
}
