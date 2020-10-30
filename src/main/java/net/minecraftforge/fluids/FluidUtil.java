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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Optional;

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
     * @param world  The world that contains the fluid handler block.
     * @param pos    The position of the fluid handler block in the world.
     * @param side   The side of the block to interact with. May be null.
     * @return true if the interaction succeeded and updated the item held by the player, false otherwise.
     */
    public static boolean interactWithFluidHandler(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull World world, @Nonnull BlockPos pos, @Nullable Direction side)
    {
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(pos);

        return getFluidHandler(world, pos, side).map(handler -> interactWithFluidHandler(player, hand, handler)).orElse(false);
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
    public static boolean interactWithFluidHandler(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull IFluidHandler handler)
    {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(hand);
        Preconditions.checkNotNull(handler);

        ItemStack heldItem = player.getHeldItem(hand);
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
                        player.setHeldItem(hand, fluidActionResult.getResult());
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
     *                    See {@link  #tryFillContainerAndStow(ItemStack, IFluidHandler, IItemHandler, int, PlayerEntity, boolean)}.
     * @param fluidSource The fluid handler to be drained.
     * @param maxAmount   The largest amount of fluid that should be transferred.
     * @param player      The player to make the filling noise. Pass null for no noise.
     * @param doFill      true if the container should actually be filled, false if it should be simulated.
     * @return a {@link FluidActionResult} holding the filled container if successful.
     */
    @Nonnull
    public static FluidActionResult tryFillContainer(@Nonnull ItemStack container, IFluidHandler fluidSource, int maxAmount, @Nullable PlayerEntity player, boolean doFill)
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
                                player.world.playSound(null, player.getPosX(), player.getPosY() + 0.5, player.getPosZ(), soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
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
     *                         See {@link #tryEmptyContainerAndStow(ItemStack, IFluidHandler, IItemHandler, int, PlayerEntity, boolean)}.
     * @param fluidDestination The fluid handler to be filled by the container.
     * @param maxAmount        The largest amount of fluid that should be transferred.
     * @param player           Player for making the bucket drained sound. Pass null for no noise.
     * @param doDrain          true if the container should actually be drained, false if it should be simulated.
     * @return a {@link FluidActionResult} holding the empty container if the fluid handler was filled.
     *         NOTE If the container is consumable, the empty container will be null on success.
     */
    @Nonnull
    public static FluidActionResult tryEmptyContainer(@Nonnull ItemStack container, IFluidHandler fluidDestination, int maxAmount, @Nullable PlayerEntity player, boolean doDrain)
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
                        player.world.playSound(null, player.getPosX(), player.getPosY() + 0.5, player.getPosZ(), soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
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
    public static FluidActionResult tryFillContainerAndStow(@Nonnull ItemStack container, IFluidHandler fluidSource, IItemHandler inventory, int maxAmount, @Nullable PlayerEntity player, boolean doFill)
    {
        if (container.isEmpty())
        {
            return FluidActionResult.FAILURE;
        }

        if (player != null && player.abilities.isCreativeMode)
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
    public static FluidActionResult tryEmptyContainerAndStow(@Nonnull ItemStack container, IFluidHandler fluidDestination, IItemHandler inventory, int maxAmount, @Nullable PlayerEntity player, boolean doDrain)
    {
        if (container.isEmpty())
        {
            return FluidActionResult.FAILURE;
        }

        if (player != null && player.abilities.isCreativeMode)
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
            if (doTransfer)
            {
                FluidStack drained = fluidSource.drain(fillableAmount, IFluidHandler.FluidAction.EXECUTE);
                if (!drained.isEmpty())
                {
                    drained.setAmount(fluidDestination.fill(drained, IFluidHandler.FluidAction.EXECUTE));
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
            return getFluidHandler(container)
                    .map(handler -> handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE));
        }
        return Optional.empty();
    }

    /**
     * Helper method to get an IFluidHandler for at a block position.
     */
    public static LazyOptional<IFluidHandler> getFluidHandler(World world, BlockPos blockPos, @Nullable Direction side)
    {
        BlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();

        if (block.hasTileEntity(state))
        {
            TileEntity tileEntity = world.getTileEntity(blockPos);
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
     * @return a {@link FluidActionResult} holding the result and the resulting container.
     */
    @Nonnull
    public static FluidActionResult tryPickUpFluid(@Nonnull ItemStack emptyContainer, @Nullable PlayerEntity playerIn, World worldIn, BlockPos pos, Direction side)
    {
        if (emptyContainer.isEmpty() || worldIn == null || pos == null)
        {
            return FluidActionResult.FAILURE;
        }

        BlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof IFluidBlock)
        {
            IFluidHandler targetFluidHandler = getFluidHandler(worldIn, pos, side).orElse(null);
            if (targetFluidHandler != null)
            {
                return tryFillContainer(emptyContainer, targetFluidHandler, Integer.MAX_VALUE, playerIn, true);
            }
        }
        return FluidActionResult.FAILURE;
    }

    /**
     * ItemStack version of {@link #tryPlaceFluid(PlayerEntity, World, BlockPos, IFluidHandler, FluidStack)}.
     * Use the returned {@link FluidActionResult} to update the container ItemStack.
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
    public static FluidActionResult tryPlaceFluid(@Nullable PlayerEntity player, World world, Hand hand, BlockPos pos, @Nonnull ItemStack container, FluidStack resource)
    {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1); // do not modify the input
        return getFluidHandler(containerCopy)
            .filter(handler -> tryPlaceFluid(player, world, hand, pos, handler, resource))
            .map(IFluidHandlerItem::getContainer)
            .map(FluidActionResult::new)
            .orElse(FluidActionResult.FAILURE);
    }

    /**
     * Tries to place a fluid resource into the world as a block and drains the fluidSource.
     * Makes a fluid emptying or vaporization sound when successful.
     * Honors the amount of fluid contained by the used container.
     * Checks if water-like fluids should vaporize like in the nether.
     *
     * Modeled after {@link ItemBucket#tryPlaceContainedLiquid(PlayerEntity, World, BlockPos)}
     *
     * @param player      Player who places the fluid. May be null for blocks like dispensers.
     * @param world       World to place the fluid in
     * @param hand
     * @param pos         The position in the world to place the fluid block
     * @param fluidSource The fluid source holding the fluidStack to place
     * @param resource    The fluidStack to place.
     * @return true if the placement was successful, false otherwise
     */
    public static boolean tryPlaceFluid(@Nullable PlayerEntity player, World world, Hand hand, BlockPos pos, IFluidHandler fluidSource, FluidStack resource)
    {
        if (world == null || pos == null)
        {
            return false;
        }

        Fluid fluid = resource.getFluid();
        if (fluid == null || !fluid.getAttributes().canBePlacedInWorld(world, pos, resource))
        {
            return false;
        }

        if (fluidSource.drain(resource, IFluidHandler.FluidAction.SIMULATE).isEmpty())
        {
            return false;
        }

        BlockItemUseContext context = new BlockItemUseContext(new ItemUseContext(player, hand, new BlockRayTraceResult(Vector3d.ZERO, Direction.UP, pos, false))); //TODO: This neds proper context...

        // check that we can place the fluid at the destination
        BlockState destBlockState = world.getBlockState(pos);
        Material destMaterial = destBlockState.getMaterial();
        boolean isDestNonSolid = !destMaterial.isSolid();
        boolean isDestReplaceable = destBlockState.isReplaceable(context);
        if (!world.isAirBlock(pos) && !isDestNonSolid && !isDestReplaceable)
        {
            return false; // Non-air, solid, unreplacable block. We can't put fluid here.
        }

        if (world.getDimensionType().isUltrawarm() && fluid.getAttributes().doesVaporize(world, pos, resource))
        {
            FluidStack result = fluidSource.drain(resource, IFluidHandler.FluidAction.EXECUTE);
            if (!result.isEmpty())
            {
                result.getFluid().getAttributes().vaporize(player, world, pos, result);
                return true;
            }
        }
        else
        {
            // This fluid handler places the fluid block when filled
            IFluidHandler handler = getFluidBlockHandler(fluid, world, pos);
            FluidStack result = tryFluidTransfer(handler, fluidSource, resource, true);
            if (!result.isEmpty())
            {
                SoundEvent soundevent = resource.getFluid().getAttributes().getEmptySound(resource);
                world.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return true;
            }
        }
        return false;
    }

    /**
     * Internal method for getting a fluid block handler for placing a fluid.
     *
     * Modders: Instead of this method, use {@link #tryPlaceFluid(PlayerEntity, World, BlockPos, ItemStack, FluidStack)}
     * or {@link #tryPlaceFluid(PlayerEntity, World, BlockPos, IFluidHandler, FluidStack)}
     */
    private static IFluidHandler getFluidBlockHandler(Fluid fluid, World world, BlockPos pos)
    {
        BlockState state = fluid.getAttributes().getBlock(world, pos, fluid.getDefaultState());
        return new BlockWrapper(state, world, pos);
    }

    /**
     * Destroys a block when a fluid is placed in the same position.
     * Modeled after {@link ItemBucket#tryPlaceContainedLiquid(PlayerEntity, World, BlockPos)}
     *
     * This is a helper method for implementing {@link IFluidBlock#place(World, BlockPos, FluidStack, boolean)}.
     *
     * @param world the world that the fluid will be placed in
     * @param pos   the location that the fluid will be placed
     */
    public static void destroyBlockOnFluidPlacement(World world, BlockPos pos)
    {
        if (!world.isRemote)
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
            else if (fluid.getRegistryName().equals(new ResourceLocation("milk")))
            {
                return new ItemStack(Items.MILK_BUCKET);
            }
        }

        return fluid.getAttributes().getBucket(fluidStack);
    }
}
