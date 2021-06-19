/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidResult;
import net.minecraftforge.fluids.capability.IFluidHandlerBlock;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import net.minecraftforge.fluids.capability.wrappers.BucketPickupHandlerWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Optional;

public class FluidUtil
{
    private FluidUtil()
    {
    }

    /**
     * Fill a destination fluid handler from a source fluid handler using a specific fluid.
     * To transfer as much as possible, use {@link Integer#MAX_VALUE} for resource.amount.
     *
     * @param fluidDestination The fluid handler to be filled.
     * @param fluidSource      The fluid handler to be drained.
     * @param resource         The fluid that should be transferred. Amount represents the maximum amount to transfer.
     * @param doTransfer       True if the transfer should actually be done, false if it should be simulated.
     * @return the fluidStack that was transferred from the source to the destination. null on failure.
     */
    @Nonnull
    public static FluidStack tryFluidTransfer(IFluidHandlerBlock fluidDestination, IFluidHandlerBlock fluidSource, FluidStack resource, boolean doTransfer)
    {
        FluidStack drainable = fluidSource.drain(resource, IFluidHandlerBlock.FluidAction.SIMULATE);
        if (!drainable.isEmpty() && resource.isFluidEqual(drainable))
        {
            return tryFluidTransferInternal(fluidDestination, fluidSource, drainable, doTransfer);
        }
        return FluidStack.EMPTY;
    }

    /**
     * Internal method for filling a destination fluid handler from a source fluid handler using a specific fluid.
     * Assumes that "drainable" can be drained from "fluidSource".
     *
     * Modders: Instead of this method, use {@link #tryFluidTransfer(IFluidHandlerBlock, IFluidHandlerBlock, FluidStack, boolean)}.
     */
    @Nonnull
    private static FluidStack tryFluidTransferInternal(IFluidHandlerBlock fluidDestination, IFluidHandlerBlock fluidSource, FluidStack drainable, boolean doTransfer)
    {
        int fillableAmount = fluidDestination.fill(drainable, IFluidHandlerBlock.FluidAction.SIMULATE);
        if (fillableAmount > 0)
        {
            if (doTransfer)
            {
                FluidStack drained = fluidSource.drain(fillableAmount, IFluidHandlerBlock.FluidAction.EXECUTE);
                if (!drained.isEmpty())
                {
                    drained.setAmount(fluidDestination.fill(drained, IFluidHandlerBlock.FluidAction.EXECUTE));
                    return drained;
                }
            }
            else
            {
                drainable.setAmount(fillableAmount);
                return drainable;
            }
        }
        return FluidStack.EMPTY;
    }

    /**
     * Fill a destination fluid handler from a source item fluid handler using a specific fluid.
     * To transfer as much as possible, use {@link Integer#MAX_VALUE} for resource.amount.
     *
     * @param fluidDestination The fluid handler to be filled.
     * @param fluidSource      The item fluid handler to be drained.
     * @param resource         The fluid that should be transferred. Amount represents the maximum amount to transfer.
     * @param doTransfer       True if the transfer should actually be done, false if it should be simulated.
     * @return the fluidStack that was transferred from the source to the destination. null on failure.
     */
    @Nonnull
    public static FluidResult tryFluidTransferItem(IFluidHandlerBlock fluidDestination, IFluidHandlerItem fluidSource, FluidStack resource, boolean doTransfer)
    {
        FluidResult drainable = fluidSource.drainItem(resource, IFluidHandlerBlock.FluidAction.SIMULATE);
        if (!drainable.getFluidStack().isEmpty() && drainable.isFluidEqual(resource))
        {
            return tryFluidTransferInternalItem(fluidDestination, fluidSource, drainable.getFluidStack(), doTransfer);
        }
        return FluidResult.EMPTY;
    }

    /**
     * Internal method for filling a destination fluid handler from a source item fluid handler using a specific fluid.
     * Assumes that "drainable" can be drained from "fluidSource".
     *
     * Modders: Instead of this method, use {@link #tryFluidTransferItem(IFluidHandlerBlock, IFluidHandlerItem, FluidStack, boolean)}.
     */
    @Nonnull
    private static FluidResult tryFluidTransferInternalItem(IFluidHandlerBlock fluidDestination, IFluidHandlerItem fluidSource, FluidStack drainable, boolean doTransfer)
    {
        int fillableAmount = fluidDestination.fill(drainable, IFluidHandlerBlock.FluidAction.SIMULATE);
        if (fillableAmount > 0)
        {
            if (doTransfer)
            {
                FluidResult drained = fluidSource.drainItem(fillableAmount, IFluidHandlerBlock.FluidAction.EXECUTE);
                if (!drained.getFluidStack().isEmpty())
                {
                    fluidDestination.fill(drainable, IFluidHandlerBlock.FluidAction.EXECUTE);
                    return drained;
                }
            }
            else
            {
                FluidResult drained = fluidSource.drainItem(fillableAmount, IFluidHandlerBlock.FluidAction.SIMULATE);
                return drained;
            }
        }
        return FluidResult.EMPTY;
    }

    /**
     * Fill a container from the given fluidSource.
     *
     * @param container   The container to be filled. Will not be modified.
     * @param fluidHandlerBlock  The fluid handler to be drained from.
     * @param maxAmount   The largest amount of fluid that should be transferred.
     * @param player      The player to make the filling noise. Pass null for no noise.
     * @param doFill      true if the container should actually be filled, false if it should be simulated.
     * @return a {@link FluidResult} holding the filled container and the amount of resource that was filled if successful.
     */

    /*
    if (!filled.isEmpty() && player != null) {
        SoundEvent soundevent = filled.getFluidStack().getFluid().getAttributes().getEmptySound(filled.getFluidStack());
        player.level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }
     */
    @Nonnull
    public static FluidResult tryFillContainer(@Nonnull ItemStack container, IFluidHandlerBlock fluidHandlerBlock, int maxAmount, @Nullable PlayerEntity player, boolean doFill)
    {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);
        return getItemFluidHandler(containerCopy)
                .map(containerFluidHandler -> {
                    FluidStack extractable = fluidHandlerBlock.drain(maxAmount, IFluidHandlerBlock.FluidAction.SIMULATE);
                    FluidResult filledPossible = containerFluidHandler.fillItem(extractable, IFluidHandlerBlock.FluidAction.SIMULATE);
                    FluidResult filled;
                    // Checks if the ItemStack can't handle the maxAmount extractable
                    if (filledPossible.getFluidAmount() < extractable.getAmount()) {
                        // Fills the ItemStack
                        filled = containerFluidHandler.fillItem(filledPossible.getFluidStack(), doFill ? IFluidHandlerBlock.FluidAction.EXECUTE : IFluidHandlerBlock.FluidAction.SIMULATE);
                        // Drains FluidStack if doFill is true
                        if (doFill)
                        {
                            fluidHandlerBlock.drain(filledPossible.getFluidStack(), IFluidHandlerBlock.FluidAction.EXECUTE);
                        }
                    }
                    else {
                        // Fills the ItemStack
                        filled = containerFluidHandler.fillItem(extractable, doFill ? IFluidHandlerBlock.FluidAction.EXECUTE : IFluidHandlerBlock.FluidAction.SIMULATE);
                        // Drains FluidStack if doFill is true
                        if (doFill)
                        {
                            fluidHandlerBlock.drain(extractable, IFluidHandlerBlock.FluidAction.EXECUTE);
                        }
                    }
                    return filled;
                }).orElse(FluidResult.EMPTY);
    }

    /**
     * Helper method to get an {@link IFluidHandlerItem} for an itemStack.
     *
     * The itemStack passed in here WILL be modified, the {@link IFluidHandlerItem} acts on it directly.
     * after using the fluid handler to get the resulting item back.
     *
     * Note that the itemStack MUST have a stackSize of 1 if you want to fill or drain it.
     * You can't fill or drain multiple items at once, if you do then liquid is multiplied or destroyed.
     *
     * Vanilla buckets will be converted to universal buckets if they are enabled.
     */
    public static LazyOptional<IFluidHandlerItem> getItemFluidHandler(@Nonnull ItemStack itemStack)
    {
        return itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
    }

    /**
     * Helper method to get the fluid contained in an itemStack
     */
    public static Optional<FluidStack> getFluidContainedInItem(@Nonnull ItemStack container)
    {
        if (!container.isEmpty())
        {
            container = ItemHandlerHelper.copyStackWithSize(container, 1);
            Optional<FluidStack> fluidContained = getItemFluidHandler(container)
                    .map(handler -> handler.drainItem(Integer.MAX_VALUE, IFluidHandlerBlock.FluidAction.SIMULATE).getFluidStack());
            if (fluidContained.isPresent() && !fluidContained.get().isEmpty())
            {
                return fluidContained;
            }
        }
        return Optional.empty();
    }

    /**
     * Helper method to get an IFluidHandlerBlock for at a block position.
     */
    public static LazyOptional<IFluidHandlerBlock> getBlockFluidHandler(World world, BlockPos blockPos, @Nullable Direction side)
    {
        BlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();

        if (block.hasTileEntity(state))
        {
            TileEntity tileEntity = world.getBlockEntity(blockPos);
            if (tileEntity != null)
            {
                return tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
            }
        }
        return LazyOptional.empty();
    }

    /**
     * Attempts to pick up a fluid in the world and put it in an empty container item.
     *
     * @param emptyContainer The empty container to fill.
     *                       Will not be modified directly, if modifications are necessary a modified copy is returned in the result.
     * @param playerIn       The player filling the container. Optional.
     * @param worldIn        The world the fluid is in.
     * @param pos            The position of the fluid in the world.
     * @param side           The side of the fluid that is being drained.
     * @return a {@link FluidResult} holding the result and the resulting container.
     */
    @Nonnull
    public static FluidResult tryPickUpFluid(@Nonnull ItemStack emptyContainer, @Nullable PlayerEntity playerIn, World worldIn, BlockPos pos, Direction side)
    {
        if (emptyContainer.isEmpty() || worldIn == null || pos == null)
        {
            return FluidResult.EMPTY;
        }

        BlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();
        IFluidHandlerBlock targetFluidHandler;
        if (block instanceof IFluidBlock)
        {
            targetFluidHandler = new FluidBlockWrapper((IFluidBlock) block, worldIn, pos);
        }
        else if (block instanceof IBucketPickupHandler)
        {
            targetFluidHandler = new BucketPickupHandlerWrapper((IBucketPickupHandler) block, worldIn, pos);
        }
        else
        {
            Optional<IFluidHandlerBlock> fluidHandler = getBlockFluidHandler(worldIn, pos, side).resolve();
            if (!fluidHandler.isPresent())
            {
                return FluidResult.EMPTY;
            }
            targetFluidHandler = fluidHandler.get();
        }
        return tryFillContainer(emptyContainer, targetFluidHandler, Integer.MAX_VALUE, playerIn, true);
    }

    /**
     * Use the returned {@link FluidResult} to update the container ItemStack.
     *
     * @param player    Player who places the fluid. May be null for blocks like dispensers.
     * @param world     World to place the fluid in
     * @param hand
     * @param pos       The position in the world to place the fluid block
     * @param container The fluid container holding the fluidStack to place
     * @param resource  The fluidStack to place
     * @return the container's ItemStack with the remaining amount of fluid if the placement was successful, null otherwise
     */
    @Nonnull
    public static FluidResult tryPlaceFluidItem(@Nullable PlayerEntity player, World world, Hand hand, BlockPos pos, @Nonnull ItemStack container, FluidStack resource)
    {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1); // do not modify the input
        return getItemFluidHandler(containerCopy)
            .map(handler -> tryPlaceFluidItemInternal(player, world, hand, pos, handler, resource))
                .orElseGet(() -> FluidResult.EMPTY);
    }

    /**
     * Tries to place a fluid resource into the world as a block and drains the fluidSource.
     * Makes a fluid emptying or vaporization sound when successful.
     * Honors the amount of fluid contained by the used container.
     * Checks if water-like fluids should vaporize like in the nether.
     *
     * Modeled after {@link net.minecraft.item.BucketItem#emptyBucket(PlayerEntity, World, BlockPos, BlockRayTraceResult)}
     *
     * @param player      Player who places the fluid. May be null for blocks like dispensers.
     * @param world       World to place the fluid in
     * @param hand
     * @param pos         The position in the world to place the fluid block
     * @param fluidSource The fluid source holding the fluidStack to place
     * @param resource    The fluidStack to place.
     * @return FluidResult which contains ItemStack and fluid removed if the placement was successful, empty FluidResult otherwise
     */
    public static FluidResult tryPlaceFluidItemInternal(@Nullable PlayerEntity player, World world, Hand hand, BlockPos pos, IFluidHandlerItem fluidSource, FluidStack resource)
    {
        if (world == null || pos == null)
        {
            return FluidResult.EMPTY;
        }

        Fluid fluid = resource.getFluid();
        if (fluid == Fluids.EMPTY || !fluid.getAttributes().canBePlacedInWorld(world, pos, resource))
        {
            return FluidResult.EMPTY;
        }

        if (fluidSource.drainItem(resource, IFluidHandlerBlock.FluidAction.SIMULATE).isEmpty())
        {
            return FluidResult.EMPTY;
        }

        BlockItemUseContext context = new BlockItemUseContext(world, player, hand, player == null ? ItemStack.EMPTY : player.getItemInHand(hand), new BlockRayTraceResult(Vector3d.ZERO, Direction.UP, pos, false));

        // check that we can place the fluid at the destination
        BlockState destBlockState = world.getBlockState(pos);
        Material destMaterial = destBlockState.getMaterial();
        boolean isDestNonSolid = !destMaterial.isSolid();
        boolean isDestReplaceable = destBlockState.canBeReplaced(context);
        boolean canDestContainFluid = destBlockState.getBlock() instanceof ILiquidContainer && ((ILiquidContainer) destBlockState.getBlock()).canPlaceLiquid(world, pos, destBlockState, fluid);
        if (!world.isEmptyBlock(pos) && !isDestNonSolid && !isDestReplaceable && !canDestContainFluid)
        {
            return FluidResult.EMPTY; // Non-air, solid, unreplacable block. We can't put fluid here.
        }

        if (world.dimensionType().ultraWarm() && fluid.getAttributes().doesVaporize(world, pos, resource))
        {
            FluidResult result = fluidSource.drainItem(resource, IFluidHandlerBlock.FluidAction.EXECUTE);
            if (!result.getFluidStack().isEmpty())
            {
                result.getFluid().getAttributes().vaporize(player, world, pos, result.getFluidStack());
                return result;
            }
        }
        else
        {
            // This fluid handler places the fluid block when filled
            IFluidHandlerBlock handler;
            if (canDestContainFluid)
            {
                handler = new BlockWrapper.LiquidContainerBlockWrapper((ILiquidContainer) destBlockState.getBlock(), world, pos);
            }
            else
            {
                handler = getFluidBlockHandler(fluid, world, pos);
            }
            FluidResult result = tryFluidTransferItem(handler, fluidSource, resource, true);
            if (!result.getFluidStack().isEmpty())
            {
                SoundEvent soundevent = resource.getFluid().getAttributes().getEmptySound(resource);
                world.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return result;
            }
        }
        return FluidResult.EMPTY;
    }

    /**
     * Internal method for getting a fluid block handler for placing a fluid.
     *
     * Modders: Instead of this method, use {@link #tryPlaceFluidItem(PlayerEntity, World, Hand, BlockPos, ItemStack, FluidStack)}
     */
    private static IFluidHandlerBlock getFluidBlockHandler(Fluid fluid, World world, BlockPos pos)
    {
        BlockState state = fluid.getAttributes().getBlock(world, pos, fluid.defaultFluidState());
        return new BlockWrapper(state, world, pos);
    }

    /**
     * Destroys a block when a fluid is placed in the same position.
     * Modeled after {@link net.minecraft.item.BucketItem#emptyBucket(PlayerEntity, World, BlockPos, BlockRayTraceResult)}
     *
     * This is a helper method for implementing {@link IFluidBlock#place(World, BlockPos, FluidStack, IFluidHandlerBlock.FluidAction)}.
     *
     * @param world the world that the fluid will be placed in
     * @param pos   the location that the fluid will be placed
     */
    public static void destroyBlockOnFluidPlacement(World world, BlockPos pos)
    {
        if (!world.isClientSide)
        {
            BlockState destBlockState = world.getBlockState(pos);
            Material destMaterial = destBlockState.getMaterial();
            boolean isDestNonSolid = !destMaterial.isSolid();
            boolean isDestReplaceable = false; //TODO: Needs BlockItemUseContext destBlockState.getBlock().isReplaceable(world, pos);
            if ((isDestNonSolid || isDestReplaceable) && !destMaterial.isLiquid())
            {
                world.destroyBlock(pos, true);
            }
        }
    }

    /**
     * @param fluidStack contents used to fill the bucket.
     *                   FluidStack is used instead of Fluid to preserve fluid NBT, the amount is ignored.
     * @return a filled vanilla bucket or filled universal bucket.
     *         Returns empty itemStack if none of the enabled buckets can hold the fluid.
     */
    @Nonnull
    public static ItemStack getFilledBucket(@Nonnull FluidStack fluidStack)
    {
        Fluid fluid = fluidStack.getFluid();

        if (!fluidStack.hasTag() || fluidStack.getTag().isEmpty())
        {
            if (fluid == Fluids.WATER)
            {
                return new ItemStack(Items.WATER_BUCKET);
            }
            else if (fluid == Fluids.LAVA)
            {
                return new ItemStack(Items.LAVA_BUCKET);
            }
        }

        return fluid.getAttributes().getBucket(fluidStack);
    }
}
