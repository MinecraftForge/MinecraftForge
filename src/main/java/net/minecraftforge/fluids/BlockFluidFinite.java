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

import java.util.Random;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;

/**
 * This is a cellular-automata based finite fluid block implementation.
 *
 * It is highly recommended that you use/extend this class for finite fluid blocks.
 *
 */
public class BlockFluidFinite extends BlockFluidBase
{
    public BlockFluidFinite(Fluid fluid, Material material, MapColor mapColor)
    {
        super(fluid, material, mapColor);
    }

    public BlockFluidFinite(Fluid fluid, Material material)
    {
        this(fluid, material, material.getMaterialMapColor());
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
        return state.getValue(LEVEL) + 1;
    }

    @Override
    public boolean canCollideCheck(@Nonnull IBlockState state, boolean fullHit)
    {
        return fullHit;
    }

    @Override
    public int getMaxRenderHeightMeta()
    {
        return quantaPerBlock - 1;
    }

    @Override
    public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand)
    {
        boolean changed = false;
        int quantaRemaining = state.getValue(LEVEL) + 1;

        // Flow vertically if possible
        int prevRemaining = quantaRemaining;
        quantaRemaining = tryToFlowVerticallyInto(world, pos, quantaRemaining);

        if (quantaRemaining < 1)
        {
            return;
        }
        else if (quantaRemaining != prevRemaining)
        {
            changed = true;
            if (quantaRemaining == 1)
            {
                world.setBlockState(pos, state.withProperty(LEVEL, quantaRemaining - 1), Constants.BlockFlags.SEND_TO_CLIENTS);
                return;
            }
        }
        else if (quantaRemaining == 1)
        {
            return;
        }

        // Flow out if possible
        int lowerThan = quantaRemaining - 1;
        int total = quantaRemaining;
        int count = 1;

        for (EnumFacing side : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos off = pos.offset(side);
            if (displaceIfPossible(world, off))
                world.setBlockToAir(off);

            int quanta = getQuantaValueBelow(world, off, lowerThan);
            if (quanta >= 0)
            {
                count++;
                total += quanta;
            }
        }

        if (count == 1)
        {
            if (changed)
            {
                world.setBlockState(pos, state.withProperty(LEVEL, quantaRemaining - 1), Constants.BlockFlags.SEND_TO_CLIENTS);
            }
            return;
        }

        int each = total / count;
        int rem = total % count;

        for (EnumFacing side : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos off = pos.offset(side);
            int quanta = getQuantaValueBelow(world, off, lowerThan);
            if (quanta >= 0)
            {
                int newQuanta = each;
                if (rem == count || rem > 1 && rand.nextInt(count - rem) != 0)
                {
                    ++newQuanta;
                    --rem;
                }

                if (newQuanta != quanta)
                {
                    if (newQuanta == 0)
                    {
                        world.setBlockToAir(off);
                    }
                    else
                    {
                        world.setBlockState(off, getDefaultState().withProperty(LEVEL, newQuanta - 1), Constants.BlockFlags.SEND_TO_CLIENTS);
                    }
                    world.scheduleUpdate(off, this, tickRate);
                }
                --count;
            }
        }

        if (rem > 0)
        {
            ++each;
        }
        world.setBlockState(pos, state.withProperty(LEVEL, each - 1), Constants.BlockFlags.SEND_TO_CLIENTS);
    }

    public int tryToFlowVerticallyInto(World world, BlockPos pos, int amtToInput)
    {
        IBlockState myState = world.getBlockState(pos);
        BlockPos other = pos.add(0, densityDir, 0);
        if (other.getY() < 0 || other.getY() >= world.getHeight())
        {
            world.setBlockToAir(pos);
            return 0;
        }

        int amt = getQuantaValueBelow(world, other, quantaPerBlock);
        if (amt >= 0)
        {
            amt += amtToInput;
            if (amt > quantaPerBlock)
            {
                world.setBlockState(other, myState.withProperty(LEVEL, quantaPerBlock - 1));
                world.scheduleUpdate(other, this, tickRate);
                return amt - quantaPerBlock;
            }
            else if (amt > 0)
            {
                world.setBlockState(other, myState.withProperty(LEVEL, amt - 1));
                world.scheduleUpdate(other, this, tickRate);
                world.setBlockToAir(pos);
                return 0;
            }
            return amtToInput;
        }
        else
        {
            int density_other = getDensity(world, other);
            if (density_other == Integer.MAX_VALUE)
            {
                if (displaceIfPossible(world, other))
                {
                    world.setBlockState(other, myState.withProperty(LEVEL, amtToInput - 1));
                    world.scheduleUpdate(other, this, tickRate);
                    world.setBlockToAir(pos);
                    return 0;
                }
                else
                {
                    return amtToInput;
                }
            }

            if (densityDir < 0)
            {
                if (density_other < density) // then swap
                {
                    IBlockState state = world.getBlockState(other);
                    world.setBlockState(other, myState.withProperty(LEVEL, amtToInput - 1));
                    world.setBlockState(pos,   state);
                    world.scheduleUpdate(other, this, tickRate);
                    world.scheduleUpdate(pos,   state.getBlock(), state.getBlock().tickRate(world));
                    return 0;
                }
            }
            else
            {
                if (density_other > density)
                {
                    IBlockState state = world.getBlockState(other);
                    world.setBlockState(other, myState.withProperty(LEVEL, amtToInput - 1));
                    world.setBlockState(pos, state);
                    world.scheduleUpdate(other, this,  tickRate);
                    world.scheduleUpdate(pos, state.getBlock(), state.getBlock().tickRate(world));
                    return 0;
                }
            }
            return amtToInput;
        }
    }

    /* IFluidBlock */
    @Override
    public int place(World world, BlockPos pos, @Nonnull FluidStack fluidStack, boolean doPlace)
    {
        IBlockState existing = world.getBlockState(pos);
        float quantaAmount = Fluid.BUCKET_VOLUME / quantaPerBlockFloat;
        // If the stack contains more available fluid than the full source block,
        // set a source block
        int closest = Fluid.BUCKET_VOLUME;
        int quanta = quantaPerBlock;
        if (fluidStack.amount < closest)
        {
            // Figure out maximum level to match stack amount
            closest = MathHelper.floor(quantaAmount * MathHelper.floor(fluidStack.amount / quantaAmount));
            quanta = MathHelper.floor(closest / quantaAmount);
        }
        if (existing.getBlock() == this)
        {
            int existingQuanta = existing.getValue(LEVEL) + 1;
            int missingQuanta = quantaPerBlock - existingQuanta;
            closest = Math.min(closest, MathHelper.floor(missingQuanta * quantaAmount));
            quanta = Math.min(quanta + existingQuanta, quantaPerBlock);
        }

        // If too little (or too much, technically impossible) fluid is to be placed, abort
        if (quanta < 1 || quanta > 16)
            return 0;

        if (doPlace)
        {
            FluidUtil.destroyBlockOnFluidPlacement(world, pos);
            world.setBlockState(pos, getDefaultState().withProperty(LEVEL, quanta - 1), Constants.BlockFlags.DEFAULT_AND_RERENDER);
        }

        return closest;
    }

    @Override
    public FluidStack drain(World world, BlockPos pos, boolean doDrain)
    {
        final FluidStack fluidStack = new FluidStack(getFluid(), MathHelper.floor(getQuantaPercentage(world, pos) * Fluid.BUCKET_VOLUME));

        if (doDrain)
        {
            world.setBlockToAir(pos);
        }

        return fluidStack;
    }

    @Override
    public boolean canDrain(World world, BlockPos pos)
    {
        return true;
    }
}
