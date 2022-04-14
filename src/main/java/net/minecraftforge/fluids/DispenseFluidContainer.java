/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.BlockSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

/**
 * Fills or drains a fluid container item using a Dispenser.
 */
public class DispenseFluidContainer extends DefaultDispenseItemBehavior
{
    private static final DispenseFluidContainer INSTANCE = new DispenseFluidContainer();

    public static DispenseFluidContainer getInstance()
    {
        return INSTANCE;
    }

    private DispenseFluidContainer() {}

    private final DefaultDispenseItemBehavior dispenseBehavior = new DefaultDispenseItemBehavior();

    @Override
    @Nonnull
    public ItemStack execute(@Nonnull BlockSource source, @Nonnull ItemStack stack)
    {
        if (FluidUtil.getFluidContained(stack).isPresent())
        {
            return dumpContainer(source, stack);
        }
        else
        {
            return fillContainer(source, stack);
        }
    }

    /**
     * Picks up fluid in front of a Dispenser and fills a container with it.
     */
    @Nonnull
    private ItemStack fillContainer(@Nonnull BlockSource source, @Nonnull ItemStack stack)
    {
        Level level = source.getLevel();
        Direction dispenserFacing = source.getBlockState().getValue(DispenserBlock.FACING);
        BlockPos blockpos = source.getPos().relative(dispenserFacing);

        FluidActionResult actionResult = FluidUtil.tryPickUpFluid(stack, null, level, blockpos, dispenserFacing.getOpposite());
        ItemStack resultStack = actionResult.getResult();

        if (!actionResult.isSuccess() || resultStack.isEmpty())
        {
            return super.execute(source, stack);
        }

        if (stack.getCount() == 1)
        {
            return resultStack;
        }
        else if (((DispenserBlockEntity)source.getEntity()).addItem(resultStack) < 0)
        {
            this.dispenseBehavior.dispense(source, resultStack);
        }

        ItemStack stackCopy = stack.copy();
        stackCopy.shrink(1);
        return stackCopy;
    }

    /**
     * Drains a filled container and places the fluid in front of the Dispenser.
     */
    @Nonnull
    private ItemStack dumpContainer(BlockSource source, @Nonnull ItemStack stack)
    {
        ItemStack singleStack = stack.copy();
        singleStack.setCount(1);
        IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(singleStack).orElse(null);
        if (fluidHandler == null)
        {
            return super.execute(source, stack);
        }

        FluidStack fluidStack = fluidHandler.drain(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
        Direction dispenserFacing = source.getBlockState().getValue(DispenserBlock.FACING);
        BlockPos blockpos = source.getPos().relative(dispenserFacing);
        FluidActionResult result = FluidUtil.tryPlaceFluid(null, source.getLevel(), InteractionHand.MAIN_HAND, blockpos, stack, fluidStack);

        if (result.isSuccess())
        {
            ItemStack drainedStack = result.getResult();

            if (drainedStack.getCount() == 1)
            {
                return drainedStack;
            }
            else if (!drainedStack.isEmpty() && ((DispenserBlockEntity)source.getEntity()).addItem(drainedStack) < 0)
            {
                this.dispenseBehavior.dispense(source, drainedStack);
            }

            ItemStack stackCopy = drainedStack.copy();
            stackCopy.shrink(1);
            return stackCopy;
        }
        else
        {
            return this.dispenseBehavior.dispense(source, stack);
        }
    }
}
