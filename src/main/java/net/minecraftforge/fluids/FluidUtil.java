/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import net.minecraftforge.fluids.capability.wrappers.BucketPickupHandlerWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Optional;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;

public class FluidUtil
{
    private FluidUtil()
    {
    }

    /**
     * Used to handle the common case of a player holding a fluid item and right-clicking on a fluid handler block.
     * First it tries to fill the item from the block,
     * if that action fails then it tries to drain the item into the block.
     * Automatically updates the item in the player's hand and stashes any extra items created.
     *
     * @param player The player doing the interaction between the item and fluid handler block.
     * @param hand   The player's hand that is holding an item that should interact with the fluid handler block.
     * @param level  The level that contains the fluid handler block.
     * @param pos    The position of the fluid handler block in the level.
     * @param side   The side of the block to interact with. May be null.
     * @return true if the interaction succeeded and updated the item held by the player, false otherwise.
     */
    public static boolean interactWithFluidHandler(@Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull Level level, @Nonnull BlockPos pos, @Nullable Direction side)
    {
        Preconditions.checkNotNull(level);
        Preconditions.checkNotNull(pos);

        return getFluidHandler(level, pos, side).map(handler -> interactWithFluidHandler(player, hand, handler)).orElse(false);
    }

    /**
     * Used to handle the common case of a player holding a fluid item and right-clicking on a fluid handler.
     * First it tries to fill the item from the handler,
     * if that action fails then it tries to drain the item into the handler.
     * Automatically updates the item in the player's hand and stashes any extra items created.
     *
     * @param player  The player doing the interaction between the item and fluid handler.
     * @param hand    The player's hand that is holding an item that should interact with the fluid handler.
     * @param handler The fluid handler.
     * @return true if the interaction succeeded and updated the item held by the player, false otherwise.
     */
    public static boolean interactWithFluidHandler(@Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull IFluidHandler handler)
    {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(hand);
        Preconditions.checkNotNull(handler);

        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty())
        {
            return player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .map(playerInventory -> {

                    FluidActionResult fluidActionResult = tryFillContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
                    if (!fluidActionResult.isSuccess())
                    {
                        fluidActionResult = tryEmptyContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
                    }

                    if (fluidActionResult.isSuccess())
                    {
                        player.setItemInHand(hand, fluidActionResult.getResult());
                        return true;
                    }
                    return false;
                })
                .orElse(false);
        }
        return false;
    }

    /**
     * Fill a container from the given fluidSource.
     *
     * @param container   The container to be filled. Will not be modified.
     *                    Separate handling must be done to reduce the stack size, stow containers, etc, on success.
     *                    See {@link #tryFillContainerAndStow(ItemStack, IFluidHandler, IItemHandler, int, Player, boolean)}.
     * @param fluidSource The fluid handler to be drained.
     * @param maxAmount   The largest amount of fluid that should be transferred.
     * @param player      The player to make the filling noise. Pass null for no noise.
     * @param doFill      true if the container should actually be filled, false if it should be simulated.
     * @return a {@link FluidActionResult} holding the filled container if successful.
     */
    @Nonnull
    public static FluidActionResult tryFillContainer(@Nonnull ItemStack container, IFluidHandler fluidSource, int maxAmount, @Nullable Player player, boolean doFill)
    {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1); // do not modify the input
        return getFluidHandler(containerCopy)
                .map(containerFluidHandler -> {
                    FluidStack simulatedTransfer = tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, false);
                    if (!simulatedTransfer.isEmpty())
                    {
                        if (doFill)
                        {
                            tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, true);
                            if (player != null)
                            {
                                SoundEvent soundevent = simulatedTransfer.getFluid().getAttributes().getFillSound(simulatedTransfer);
                                player.level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                            }
                        }
                        else
                        {
                            containerFluidHandler.fill(simulatedTransfer, IFluidHandler.FluidAction.SIMULATE);
                        }

                        ItemStack resultContainer = containerFluidHandler.getContainer();
                        return new FluidActionResult(resultContainer);
                    }
                    return FluidActionResult.FAILURE;
                })
                .orElse(FluidActionResult.FAILURE);
    }

    /**
     * Takes a filled container and tries to empty it into the given tank.
     *
     * @param container        The filled container. Will not be modified.
     *                         Separate handling must be done to reduce the stack size, stow containers, etc, on success.
     *                         See {@link #tryEmptyContainerAndStow(ItemStack, IFluidHandler, IItemHandler, int, Player, boolean)}.
     * @param fluidDestination The fluid handler to be filled by the container.
     * @param maxAmount        The largest amount of fluid that should be transferred.
     * @param player           Player for making the bucket drained sound. Pass null for no noise.
     * @param doDrain          true if the container should actually be drained, false if it should be simulated.
     * @return a {@link FluidActionResult} holding the empty container if the fluid handler was filled.
     *         NOTE If the container is consumable, the empty container will be null on success.
     */
    @Nonnull
    public static FluidActionResult tryEmptyContainer(@Nonnull ItemStack container, IFluidHandler fluidDestination, int maxAmount, @Nullable Player player, boolean doDrain)
    {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1); // do not modify the input
        return getFluidHandler(containerCopy)
                .map(containerFluidHandler -> {

                    // We are acting on a COPY of the stack, so performing changes is acceptable even if we are simulating.
                    FluidStack transfer = tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, true);
                    if (transfer.isEmpty())
                        return FluidActionResult.FAILURE;

                    if (doDrain && player != null)
                    {
                        SoundEvent soundevent = transfer.getFluid().getAttributes().getEmptySound(transfer);
                        player.level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }

                    ItemStack resultContainer = containerFluidHandler.getContainer();
                    return new FluidActionResult(resultContainer);
                })
                .orElse(FluidActionResult.FAILURE);
    }

    /**
     * Takes an Fluid Container Item and tries to fill it from the given tank.
     * If the player is in creative mode, the container will not be modified on success, and no additional items created.
     * If the input itemstack has a stacksize > 1 it will stow the filled container in the given inventory.
     * If the inventory does not accept it, it will be given to the player or dropped at the players feet.
     *      If player is null in this case, the action will be aborted.
     *
     * @param container   The Fluid Container ItemStack to fill.
     *                    Will not be modified directly, if modifications are necessary a modified copy is returned in the result.
     * @param fluidSource The fluid source to fill from
     * @param inventory   An inventory where any additionally created item (filled container if multiple empty are present) are put
     * @param maxAmount   Maximum amount of fluid to take from the tank.
     * @param player      The player that gets the items the inventory can't take.
     *                    Can be null, only used if the inventory cannot take the filled stack.
     * @param doFill      true if the container should actually be filled, false if it should be simulated.
     * @return a {@link FluidActionResult} holding the result and the resulting container. The resulting container is empty on failure.
     */
    @Nonnull
    public static FluidActionResult tryFillContainerAndStow(@Nonnull ItemStack container, IFluidHandler fluidSource, IItemHandler inventory, int maxAmount, @Nullable Player player, boolean doFill)
    {
        if (container.isEmpty())
        {
            return FluidActionResult.FAILURE;
        }

        if (player != null && player.getAbilities().instabuild)
        {
            FluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, doFill);
            if (filledReal.isSuccess())
            {
                return new FluidActionResult(container); // creative mode: item does not change
            }
        }
        else if (container.getCount() == 1) // don't need to stow anything, just fill the container stack
        {
            FluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, doFill);
            if (filledReal.isSuccess())
            {
                return filledReal;
            }
        }
        else
        {
            FluidActionResult filledSimulated = tryFillContainer(container, fluidSource, maxAmount, player, false);
            if (filledSimulated.isSuccess())
            {
                // check if we can give the itemStack to the inventory
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, filledSimulated.getResult(), true);
                if (remainder.isEmpty() || player != null)
                {
                    FluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, doFill);
                    remainder = ItemHandlerHelper.insertItemStacked(inventory, filledReal.getResult(), !doFill);

                    // give it to the player or drop it at their feet
                    if (!remainder.isEmpty() && player != null && doFill)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, remainder);
                    }

                    ItemStack containerCopy = container.copy();
                    containerCopy.shrink(1);
                    return new FluidActionResult(containerCopy);
                }
            }
        }

        return FluidActionResult.FAILURE;
    }

    /**
     * Takes an Fluid Container Item, tries to empty it into the fluid handler, and stows it in the given inventory.
     * If the player is in creative mode, the container will not be modified on success, and no additional items created.
     * If the input itemstack has a stacksize > 1 it will stow the emptied container in the given inventory.
     * If the inventory does not accept the emptied container, it will be given to the player or dropped at the players feet.
     *      If player is null in this case, the action will be aborted.
     *
     * @param container        The filled Fluid Container Itemstack to empty.
     *                         Will not be modified directly, if modifications are necessary a modified copy is returned in the result.
     * @param fluidDestination The fluid destination to fill from the fluid container.
     * @param inventory        An inventory where any additionally created item (filled container if multiple empty are present) are put
     * @param maxAmount        Maximum amount of fluid to take from the tank.
     * @param player           The player that gets the items the inventory can't take. Can be null, only used if the inventory cannot take the filled stack.
     * @param doDrain          true if the container should actually be drained, false if it should be simulated.
     * @return a {@link FluidActionResult} holding the result and the resulting container. The resulting container is empty on failure.
     */
    @Nonnull
    public static FluidActionResult tryEmptyContainerAndStow(@Nonnull ItemStack container, IFluidHandler fluidDestination, IItemHandler inventory, int maxAmount, @Nullable Player player, boolean doDrain)
    {
        if (container.isEmpty())
        {
            return FluidActionResult.FAILURE;
        }

        if (player != null && player.getAbilities().instabuild)
        {
            FluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
            if (emptiedReal.isSuccess())
            {
                return new FluidActionResult(container); // creative mode: item does not change
            }
        }
        else if (container.getCount() == 1) // don't need to stow anything, just fill and edit the container stack
        {
            FluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
            if (emptiedReal.isSuccess())
            {
                return emptiedReal;
            }
        }
        else
        {
            FluidActionResult emptiedSimulated = tryEmptyContainer(container, fluidDestination, maxAmount, player, false);
            if (emptiedSimulated.isSuccess())
            {
                // check if we can give the itemStack to the inventory
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedSimulated.getResult(), true);
                if (remainder.isEmpty() || player != null)
                {
                    FluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
                    remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedReal.getResult(), !doDrain);

                    // give it to the player or drop it at their feet
                    if (!remainder.isEmpty() && player != null && doDrain)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, remainder);
                    }

                    ItemStack containerCopy = container.copy();
                    containerCopy.shrink(1);
                    return new FluidActionResult(containerCopy);
                }
            }
        }

        return FluidActionResult.FAILURE;
    }

    /**
     * Fill a destination fluid handler from a source fluid handler with a max amount.
     * To specify a fluid to transfer instead of max amount, use {@link #tryFluidTransfer(IFluidHandler, IFluidHandler, FluidStack, boolean)}
     * To transfer as much as possible, use {@link Integer#MAX_VALUE} for maxAmount.
     *
     * @param fluidDestination The fluid handler to be filled.
     * @param fluidSource      The fluid handler to be drained.
     * @param maxAmount        The largest amount of fluid that should be transferred.
     * @param doTransfer       True if the transfer should actually be done, false if it should be simulated.
     * @return the fluidStack that was transferred from the source to the destination. null on failure.
     */
    @Nonnull
    public static FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, int maxAmount, boolean doTransfer)
    {
        FluidStack drainable = fluidSource.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
        if (!drainable.isEmpty())
        {
            return tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer);
        }
        return FluidStack.EMPTY;
    }

    /**
     * Fill a destination fluid handler from a source fluid handler using a specific fluid.
     * To specify a max amount to transfer instead of specific fluid, use {@link #tryFluidTransfer(IFluidHandler, IFluidHandler, int, boolean)}
     * To transfer as much as possible, use {@link Integer#MAX_VALUE} for resource.amount.
     *
     * @param fluidDestination The fluid handler to be filled.
     * @param fluidSource      The fluid handler to be drained.
     * @param resource         The fluid that should be transferred. Amount represents the maximum amount to transfer.
     * @param doTransfer       True if the transfer should actually be done, false if it should be simulated.
     * @return the fluidStack that was transferred from the source to the destination. null on failure.
     */
    @Nonnull
    public static FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, FluidStack resource, boolean doTransfer)
    {
        FluidStack drainable = fluidSource.drain(resource, IFluidHandler.FluidAction.SIMULATE);
        if (!drainable.isEmpty() && resource.isFluidEqual(drainable))
        {
            return tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer);
        }
        return FluidStack.EMPTY;
    }

    /**
     * Internal method for filling a destination fluid handler from a source fluid handler using a specific fluid.
     * Assumes that "drainable" can be drained from "fluidSource".
     *
     * Modders: Instead of this method, use {@link #tryFluidTransfer(IFluidHandler, IFluidHandler, FluidStack, boolean)}
     * or {@link #tryFluidTransfer(IFluidHandler, IFluidHandler, int, boolean)}.
     */
    @Nonnull
    private static FluidStack tryFluidTransfer_Internal(IFluidHandler fluidDestination, IFluidHandler fluidSource, FluidStack drainable, boolean doTransfer)
    {
        int fillableAmount = fluidDestination.fill(drainable, IFluidHandler.FluidAction.SIMULATE);
        if (fillableAmount > 0)
        {
            drainable.setAmount(fillableAmount);
            if (doTransfer)
            {
                FluidStack drained = fluidSource.drain(drainable, IFluidHandler.FluidAction.EXECUTE);
                if (!drained.isEmpty())
                {
                    drained.setAmount(fluidDestination.fill(drained, IFluidHandler.FluidAction.EXECUTE));
                    return drained;
                }
            }
            else
            {
                return drainable;
            }
        }
        return FluidStack.EMPTY;
    }

    /**
     * Helper method to get an {@link IFluidHandlerItem} for an itemStack.
     *
     * The itemStack passed in here WILL be modified, the {@link IFluidHandlerItem} acts on it directly.
     * Some {@link IFluidHandlerItem} will change the item entirely, always use {@link IFluidHandlerItem#getContainer()}
     * after using the fluid handler to get the resulting item back.
     *
     * Note that the itemStack MUST have a stackSize of 1 if you want to fill or drain it.
     * You can't fill or drain multiple items at once, if you do then liquid is multiplied or destroyed.
     *
     * Vanilla buckets will be converted to universal buckets if they are enabled.
     */
    public static LazyOptional<IFluidHandlerItem> getFluidHandler(@Nonnull ItemStack itemStack)
    {
        return itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
    }

    /**
     * Helper method to get the fluid contained in an itemStack
     */
    public static Optional<FluidStack> getFluidContained(@Nonnull ItemStack container)
    {
        if (!container.isEmpty())
        {
            container = ItemHandlerHelper.copyStackWithSize(container, 1);
            Optional<FluidStack> fluidContained = getFluidHandler(container)
                    .map(handler -> handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE));
            if (fluidContained.isPresent() && !fluidContained.get().isEmpty())
            {
                return fluidContained;
            }
        }
        return Optional.empty();
    }

    /**
     * Helper method to get an IFluidHandler for at a block position.
     */
    public static LazyOptional<IFluidHandler> getFluidHandler(Level level, BlockPos blockPos, @Nullable Direction side)
    {
        BlockState state = level.getBlockState(blockPos);
        Block block = state.getBlock();

        if (state.hasBlockEntity())
        {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity != null)
            {
                return blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
            }
        }
        return LazyOptional.empty();
    }

    /**
     * Attempts to pick up a fluid in the level and put it in an empty container item.
     *
     * @param emptyContainer The empty container to fill.
     *                       Will not be modified directly, if modifications are necessary a modified copy is returned in the result.
     * @param playerIn       The player filling the container. Optional.
     * @param level          The level the fluid is in.
     * @param pos            The position of the fluid in the level.
     * @param side           The side of the fluid that is being drained.
     * @return a {@link FluidActionResult} holding the result and the resulting container.
     */
    @Nonnull
    public static FluidActionResult tryPickUpFluid(@Nonnull ItemStack emptyContainer, @Nullable Player playerIn, Level level, BlockPos pos, Direction side)
    {
        if (emptyContainer.isEmpty() || level == null || pos == null)
        {
            return FluidActionResult.FAILURE;
        }

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        IFluidHandler targetFluidHandler;
        if (block instanceof IFluidBlock)
        {
            targetFluidHandler = new FluidBlockWrapper((IFluidBlock) block, level, pos);
        }
        else if (block instanceof BucketPickup)
        {
            targetFluidHandler = new BucketPickupHandlerWrapper((BucketPickup) block, level, pos);
        }
        else
        {
            Optional<IFluidHandler> fluidHandler = getFluidHandler(level, pos, side).resolve();
            if (!fluidHandler.isPresent())
            {
                return FluidActionResult.FAILURE;
            }
            targetFluidHandler = fluidHandler.get();
        }
        return tryFillContainer(emptyContainer, targetFluidHandler, Integer.MAX_VALUE, playerIn, true);
    }

    /**
     * ItemStack version of {@link #tryPlaceFluid(Player, Level, InteractionHand, BlockPos, IFluidHandler, FluidStack)}.
     * Use the returned {@link FluidActionResult} to update the container ItemStack.
     *
     * @param player    Player who places the fluid. May be null for blocks like dispensers.
     * @param level     Level to place the fluid in
     * @param hand      hand of the player to place the fluid with
     * @param pos       The position in the level to place the fluid block
     * @param container The fluid container holding the fluidStack to place
     * @param resource  The fluidStack to place
     * @return the container's ItemStack with the remaining amount of fluid if the placement was successful, null otherwise
     */
    @Nonnull
    public static FluidActionResult tryPlaceFluid(@Nullable Player player, Level level, InteractionHand hand, BlockPos pos, @Nonnull ItemStack container, FluidStack resource)
    {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1); // do not modify the input
        return getFluidHandler(containerCopy)
            .filter(handler -> tryPlaceFluid(player, level, hand, pos, handler, resource))
            .map(IFluidHandlerItem::getContainer)
            .map(FluidActionResult::new)
            .orElse(FluidActionResult.FAILURE);
    }

    /**
     * Tries to place a fluid resource into the level as a block and drains the fluidSource.
     * Makes a fluid emptying or vaporization sound when successful.
     * Honors the amount of fluid contained by the used container.
     * Checks if water-like fluids should vaporize like in the nether.
     *
     * Modeled after {@link BucketItem#emptyContents(Player, Level, BlockPos, BlockHitResult)}
     *
     * @param player      Player who places the fluid. May be null for blocks like dispensers.
     * @param level       Level to place the fluid in
     * @param hand        hand of the player to place the fluid with
     * @param pos         The position in the level to place the fluid block
     * @param fluidSource The fluid source holding the fluidStack to place
     * @param resource    The fluidStack to place.
     * @return true if the placement was successful, false otherwise
     */
    public static boolean tryPlaceFluid(@Nullable Player player, Level level, InteractionHand hand, BlockPos pos, IFluidHandler fluidSource, FluidStack resource)
    {
        if (level == null || pos == null)
        {
            return false;
        }

        Fluid fluid = resource.getFluid();
        if (fluid == Fluids.EMPTY || !fluid.getAttributes().canBePlacedInWorld(level, pos, resource))
        {
            return false;
        }

        if (fluidSource.drain(resource, IFluidHandler.FluidAction.SIMULATE).isEmpty())
        {
            return false;
        }

        BlockPlaceContext context = new BlockPlaceContext(level, player, hand, player == null ? ItemStack.EMPTY : player.getItemInHand(hand), new BlockHitResult(Vec3.ZERO, Direction.UP, pos, false));

        // check that we can place the fluid at the destination
        BlockState destBlockState = level.getBlockState(pos);
        Material destMaterial = destBlockState.getMaterial();
        boolean isDestNonSolid = !destMaterial.isSolid();
        boolean isDestReplaceable = destBlockState.canBeReplaced(context);
        boolean canDestContainFluid = destBlockState.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer) destBlockState.getBlock()).canPlaceLiquid(level, pos, destBlockState, fluid);
        if (!level.isEmptyBlock(pos) && !isDestNonSolid && !isDestReplaceable && !canDestContainFluid)
        {
            return false; // Non-air, solid, unreplacable block. We can't put fluid here.
        }

        if (level.dimensionType().ultraWarm() && fluid.getAttributes().doesVaporize(level, pos, resource))
        {
            FluidStack result = fluidSource.drain(resource, IFluidHandler.FluidAction.EXECUTE);
            if (!result.isEmpty())
            {
                result.getFluid().getAttributes().vaporize(player, level, pos, result);
                return true;
            }
        }
        else
        {
            // This fluid handler places the fluid block when filled
            IFluidHandler handler;
            if (canDestContainFluid)
            {
                handler = new BlockWrapper.LiquidContainerBlockWrapper((LiquidBlockContainer) destBlockState.getBlock(), level, pos);
            }
            else
            {
                handler = getFluidBlockHandler(fluid, level, pos);
            }
            FluidStack result = tryFluidTransfer(handler, fluidSource, resource, true);
            if (!result.isEmpty())
            {
                SoundEvent soundevent = resource.getFluid().getAttributes().getEmptySound(resource);
                level.playSound(player, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
                return true;
            }
        }
        return false;
    }

    /**
     * Internal method for getting a fluid block handler for placing a fluid.
     *
     * Modders: Instead of this method, use {@link #tryPlaceFluid(Player, Level, InteractionHand, BlockPos, ItemStack, FluidStack)}
     * or {@link #tryPlaceFluid(Player, Level, InteractionHand, BlockPos, IFluidHandler, FluidStack)}
     */
    private static IFluidHandler getFluidBlockHandler(Fluid fluid, Level level, BlockPos pos)
    {
        BlockState state = fluid.getAttributes().getBlock(level, pos, fluid.defaultFluidState());
        return new BlockWrapper(state, level, pos);
    }

    /**
     * Destroys a block when a fluid is placed in the same position.
     * Modeled after {@link BucketItem#emptyContents(Player, Level, BlockPos, BlockHitResult)}
     *
     * This is a helper method for implementing {@link IFluidBlock#place(Level, BlockPos, FluidStack, IFluidHandler.FluidAction)}.
     *
     * @param level the level that the fluid will be placed in
     * @param pos   the location that the fluid will be placed
     */
    public static void destroyBlockOnFluidPlacement(Level level, BlockPos pos)
    {
        if (!level.isClientSide)
        {
            BlockState destBlockState = level.getBlockState(pos);
            Material destMaterial = destBlockState.getMaterial();
            boolean isDestNonSolid = !destMaterial.isSolid();
            boolean isDestReplaceable = false; //TODO: Needs BlockItemUseContext destBlockState.getBlock().isReplaceable(level, pos);
            if ((isDestNonSolid || isDestReplaceable) && !destMaterial.isLiquid())
            {
                level.destroyBlock(pos, true);
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
