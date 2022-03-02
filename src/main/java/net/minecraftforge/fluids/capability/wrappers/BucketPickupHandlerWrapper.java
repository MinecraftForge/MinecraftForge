/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability.wrappers;

import javax.annotation.Nonnull;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BucketPickupHandlerWrapper implements IFluidHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    protected final BucketPickup bucketPickupHandler;
    protected final Level world;
    protected final BlockPos blockPos;

    public BucketPickupHandlerWrapper(BucketPickup bucketPickupHandler, Level world, BlockPos blockPos)
    {
        this.bucketPickupHandler = bucketPickupHandler;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public int getTanks()
    {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank)
    {
        if (tank == 0)
        {
            //Best guess at stored fluid
            FluidState fluidState = world.getFluidState(blockPos);
            if (!fluidState.isEmpty())
            {
                return new FluidStack(fluidState.getType(), FluidAttributes.BUCKET_VOLUME);
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return FluidAttributes.BUCKET_VOLUME;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack)
    {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (!resource.isEmpty() && FluidAttributes.BUCKET_VOLUME <= resource.getAmount())
        {
            FluidState fluidState = world.getFluidState(blockPos);
            if (!fluidState.isEmpty() && resource.getFluid() == fluidState.getType())
            {
                if (action.execute())
                {
                    ItemStack itemStack = bucketPickupHandler.pickupBlock(world, blockPos, world.getBlockState(blockPos));
                    if (itemStack != ItemStack.EMPTY && itemStack.getItem() instanceof BucketItem bucket)
                    {
                        FluidStack extracted = new FluidStack(bucket.getFluid(), FluidAttributes.BUCKET_VOLUME);
                        if (!resource.isFluidEqual(extracted))
                        {
                            //Be loud if something went wrong
                            LOGGER.error("Fluid removed without successfully being picked up. Fluid {} at {} in {} matched requested type, but after performing pickup was {}.",
                                  fluidState.getType().getRegistryName(), blockPos, world.dimension().location(), bucket.getFluid().getRegistryName());
                            return FluidStack.EMPTY;
                        }
                        return extracted;
                    }
                }
                else
                {
                    FluidStack extracted = new FluidStack(fluidState.getType(), FluidAttributes.BUCKET_VOLUME);
                    if (resource.isFluidEqual(extracted))
                    {
                        //Validate NBT matches
                        return extracted;
                    }
                }
            }
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        if (FluidAttributes.BUCKET_VOLUME <= maxDrain)
        {
            FluidState fluidState = world.getFluidState(blockPos);
            if (!fluidState.isEmpty())
            {
                if (action.simulate())
                {
                    return new FluidStack(fluidState.getType(), FluidAttributes.BUCKET_VOLUME);
                }
                ItemStack itemStack = bucketPickupHandler.pickupBlock(world, blockPos, world.getBlockState(blockPos));
                if (itemStack != ItemStack.EMPTY && itemStack.getItem() instanceof BucketItem bucket)
                {
                    return new FluidStack(bucket.getFluid(), FluidAttributes.BUCKET_VOLUME);
                }
            }
        }
        return FluidStack.EMPTY;
    }
}
