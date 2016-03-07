
package net.minecraftforge.fluids;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * This is a cellular-automata based finite fluid block implementation.
 *
 * It is highly recommended that you use/extend this class for finite fluid blocks.
 *
 * @author OvermindDL1, KingLemming
 *
 */
public class BlockFluidFinite extends BlockFluidBase
{
    public BlockFluidFinite(Fluid fluid, Material material)
    {
        super(fluid, material);
    }

    @Override
    public int getQuantaValue(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock().isAir(world, pos))
        {
            return 0;
        }

        if (state.getBlock() != this)
        {
            return -1;
        }
        return ((Integer)state.getValue(LEVEL)) + 1;
    }

    @Override
    public boolean canCollideCheck(IBlockState state, boolean fullHit)
    {
        return fullHit && ((Integer)state.getValue(LEVEL)) == quantaPerBlock - 1;
    }

    @Override
    public int getMaxRenderHeightMeta()
    {
        return quantaPerBlock - 1;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        boolean changed = false;
        int quantaRemaining = ((Integer)state.getValue(LEVEL)) + 1;

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
                world.setBlockState(pos, state.withProperty(LEVEL, quantaRemaining - 1), 2);
                return;
            }
        }
        else if (quantaRemaining == 1)
        {
            return;
        }

        // Flow out if possible
        int lowerthan = quantaRemaining - 1;
        int total = quantaRemaining;
        int count = 1;

        for (EnumFacing side : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos off = pos.offset(side);
            if (displaceIfPossible(world, off))
                world.setBlockToAir(off);

            int quanta = getQuantaValueBelow(world, off, lowerthan);
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
                world.setBlockState(pos, state.withProperty(LEVEL, quantaRemaining - 1), 2);
            }
            return;
        }

        int each = total / count;
        int rem = total % count;

        for (EnumFacing side : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos off = pos.offset(side);
            int quanta = getQuantaValueBelow(world, off, lowerthan);
            if (quanta >= 0)
            {
                int newquanta = each;
                if (rem == count || rem > 1 && rand.nextInt(count - rem) != 0)
                {
                    ++newquanta;
                    --rem;
                }

                if (newquanta != quanta)
                {
                    if (newquanta == 0)
                    {
                        world.setBlockToAir(off);
                    }
                    else
                    {
                        world.setBlockState(off, getDefaultState().withProperty(LEVEL, newquanta - 1), 2);
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
        world.setBlockState(pos, state.withProperty(LEVEL, each - 1), 2);
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
                world.setBlockState(other, myState.withProperty(LEVEL, quantaPerBlock - 1), 3);
                world.scheduleUpdate(other, this, tickRate);
                return amt - quantaPerBlock;
            }
            else if (amt > 0)
            {
                world.setBlockState(other, myState.withProperty(LEVEL, amt - 1), 3);
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
                    world.setBlockState(other, myState.withProperty(LEVEL, amtToInput - 1), 3);
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
                    world.setBlockState(other, myState.withProperty(LEVEL, amtToInput - 1), 3);
                    world.setBlockState(pos,   state, 3);
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
                    world.setBlockState(other, myState.withProperty(LEVEL, amtToInput - 1), 3);
                    world.setBlockState(other, state, 3);
                    world.scheduleUpdate(other, this,  tickRate);
                    world.scheduleUpdate(other, state.getBlock(), state.getBlock().tickRate(world));
                    return 0;
                }
            }
            return amtToInput;
        }
    }

    /* IFluidBlock */
    @Override
    public FluidStack drain(World world, BlockPos pos, boolean doDrain)
    {
        final FluidStack fluidStack = new FluidStack(getFluid(),
                MathHelper.floor_float(getQuantaPercentage(world, pos) * FluidContainerRegistry.BUCKET_VOLUME));

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