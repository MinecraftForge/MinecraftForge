/*
 * Minecraft Forge
 * Copyright (c) 2016.
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
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.VoidFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

public class FluidUtil
{
    private FluidUtil()
    {
    }

    /**
     * Used to handle the common case of a player holding a fluid item and right-clicking on a fluid handler.
     * First it tries to fill the container item from the fluid handler,
     * if that action fails then it tries to drain the container item into the fluid handler.
     *
     * @param stack        The filled or empty fluid container.
     *                     Will not be modified directly, if modifications are necessary a modified copy is returned in the result.
     * @param fluidHandler The fluid handler to interact with.
     * @param player       The player doing the interaction between the item and fluid handler.
     * @return a {@link FluidActionResult} holding the result and resulting container.
     */
    @Nonnull
    public static FluidActionResult interactWithFluidHandler(@Nonnull ItemStack stack, IFluidHandler fluidHandler, EntityPlayer player)
    {
        if (stack.func_190926_b() || fluidHandler == null || player == null)
        {
            return FluidActionResult.FAILURE;
        }

        IItemHandler playerInventory = new InvWrapper(player.inventory);

        FluidActionResult fillResult = tryFillContainerAndStow(stack, fluidHandler, playerInventory, Integer.MAX_VALUE, player);
        if (fillResult.isSuccess())
        {
            return fillResult;
        }
        else
        {
            return tryEmptyContainerAndStow(stack, fluidHandler, playerInventory, Integer.MAX_VALUE, player);
        }
    }

    /**
     * Fill a container from the given fluidSource.
     *
     * @param container   The container to be filled. Will not be modified.
     *                    Separate handling must be done to reduce the stack size, stow containers, etc, on success.
     *                    See {@link  #tryFillContainerAndStow(ItemStack, IFluidHandler, IItemHandler, int, EntityPlayer)}.
     * @param fluidSource The fluid handler to be drained.
     * @param maxAmount   The largest amount of fluid that should be transferred.
     * @param player      The player to make the filling noise. Pass null for no noise.
     * @param doFill      true if the container should actually be filled, false if it should be simulated.
     * @return a {@link FluidActionResult} holding the filled container if successful.
     */
    @Nonnull
    public static FluidActionResult tryFillContainer(@Nonnull ItemStack container, IFluidHandler fluidSource, int maxAmount, @Nullable EntityPlayer player, boolean doFill)
    {
        ItemStack containerCopy = container.copy(); // do not modify the input
        containerCopy.func_190920_e(1);
        IFluidHandlerItem containerFluidHandler = getFluidHandler(containerCopy);
        if (containerFluidHandler != null)
        {
            FluidStack simulatedTransfer = tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, false);
            if (simulatedTransfer != null)
            {
                if (doFill)
                {
                    tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, true);
                    if (player != null)
                    {
                        SoundEvent soundevent = simulatedTransfer.getFluid().getFillSound(simulatedTransfer);
                        player.playSound(soundevent, 1f, 1f);
                    }
                }
                else
                {
                    containerFluidHandler.fill(simulatedTransfer, true);
                }

                ItemStack resultContainer = containerFluidHandler.getContainer();
                return new FluidActionResult(resultContainer);
            }
        }
        return FluidActionResult.FAILURE;
    }

    /**
     * Takes a filled container and tries to empty it into the given tank.
     *
     * @param container        The filled container. Will not be modified.
     *                         Separate handling must be done to reduce the stack size, stow containers, etc, on success.
     *                         See {@link #tryEmptyContainerAndStow(ItemStack, IFluidHandler, IItemHandler, int, EntityPlayer)}.
     * @param fluidDestination The fluid handler to be filled by the container.
     * @param maxAmount        The largest amount of fluid that should be transferred.
     * @param player           Player for making the bucket drained sound. Pass null for no noise.
     * @param doDrain          true if the container should actually be drained, false if it should be simulated.
     * @return a {@link FluidActionResult} holding the empty container if the fluid handler was filled.
     *         NOTE If the container is consumable, the empty container will be null on success.
     */
    @Nonnull
    public static FluidActionResult tryEmptyContainer(@Nonnull ItemStack container, IFluidHandler fluidDestination, int maxAmount, @Nullable EntityPlayer player, boolean doDrain)
    {
        ItemStack containerCopy = container.copy(); // do not modify the input
        containerCopy.func_190920_e(1);
        IFluidHandlerItem containerFluidHandler = getFluidHandler(containerCopy);
        if (containerFluidHandler != null)
        {
            FluidStack simulatedTransfer = tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, false);
            if (simulatedTransfer != null)
            {
                if (doDrain)
                {
                    tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, true);
                    if (player != null)
                    {
                        SoundEvent soundevent = simulatedTransfer.getFluid().getEmptySound(simulatedTransfer);
                        player.playSound(soundevent, 1f, 1f);
                    }
                }
                else
                {
                    containerFluidHandler.drain(simulatedTransfer, true);
                }

                ItemStack resultContainer = containerFluidHandler.getContainer();
                return new FluidActionResult(resultContainer);
            }
        }
        return FluidActionResult.FAILURE;
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
     * @return a {@link FluidActionResult} holding the result and the resulting container. The resulting container is empty on failure.
     */
    @Nonnull
    public static FluidActionResult tryFillContainerAndStow(@Nonnull ItemStack container, IFluidHandler fluidSource, IItemHandler inventory, int maxAmount, @Nullable EntityPlayer player)
    {
        if (container.func_190926_b())
        {
            return FluidActionResult.FAILURE;
        }

        if (player != null && player.capabilities.isCreativeMode)
        {
            FluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, true);
            if (filledReal.isSuccess())
            {
                return new FluidActionResult(container); // creative mode: item does not change
            }
        }
        else if (container.func_190916_E() == 1) // don't need to stow anything, just fill the container stack
        {
            FluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, true);
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
                if (remainder.func_190926_b() || player != null)
                {
                    FluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, true);
                    remainder = ItemHandlerHelper.insertItemStacked(inventory, filledReal.getResult(), false);

                    // give it to the player or drop it at their feet
                    if (!remainder.func_190926_b() && player != null)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, remainder);
                    }

                    ItemStack containerCopy = container.copy();
                    containerCopy.func_190918_g(1);
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
     * @return a {@link FluidActionResult} holding the result and the resulting container. The resulting container is empty on failure.
     */
    @Nonnull
    public static FluidActionResult tryEmptyContainerAndStow(@Nonnull ItemStack container, IFluidHandler fluidDestination, IItemHandler inventory, int maxAmount, @Nullable EntityPlayer player)
    {
        if (container.func_190926_b())
        {
            return FluidActionResult.FAILURE;
        }

        if (player != null && player.capabilities.isCreativeMode)
        {
            FluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, true);
            if (emptiedReal.isSuccess())
            {
                return new FluidActionResult(container); // creative mode: item does not change
            }
        }
        else if (container.func_190916_E() == 1) // don't need to stow anything, just fill and edit the container stack
        {
            FluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, true);
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
                if (remainder.func_190926_b() || player != null)
                {
                    FluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, true);
                    remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedReal.getResult(), false);

                    // give it to the player or drop it at their feet
                    if (!remainder.func_190926_b() && player != null)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, remainder);
                    }

                    ItemStack containerCopy = container.copy();
                    containerCopy.func_190918_g(1);
                    return new FluidActionResult(containerCopy);
                }
            }
        }

        return FluidActionResult.FAILURE;
    }

    /**
     * Fill a destination fluid handler from a source fluid handler.
     *
     * @param fluidDestination The fluid handler to be filled.
     * @param fluidSource      The fluid handler to be drained.
     * @param maxAmount        The largest amount of fluid that should be transferred.
     * @param doTransfer       True if the transfer should actually be done, false if it should be simulated.
     * @return the fluidStack that was transferred from the source to the destination. null on failure.
     */
    @Nullable
    public static FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, int maxAmount, boolean doTransfer)
    {
        FluidStack drainable = fluidSource.drain(maxAmount, false);
        if (drainable != null && drainable.amount > 0)
        {
            int fillableAmount = fluidDestination.fill(drainable, false);
            if (fillableAmount > 0)
            {
                if (doTransfer)
                {
                    FluidStack drained = fluidSource.drain(fillableAmount, true);
                    if (drained != null)
                    {
                        drained.amount = fluidDestination.fill(drained, true);
                        return drained;
                    }
                }
                else
                {
                    drainable.amount = fillableAmount;
                    return drainable;
                }
            }
        }
        return null;
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
     *
     * Returns null if the itemStack passed in does not have a fluid handler.
     */
    @Nullable
    public static IFluidHandlerItem getFluidHandler(@Nonnull ItemStack itemStack)
    {
        if (itemStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
        {
            return itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        }
        else
        {
            return null;
        }
    }

    /**
     * Helper method to get the fluid contained in an itemStack
     */
    @Nullable
    public static FluidStack getFluidContained(@Nonnull ItemStack container)
    {
        if (!container.func_190926_b())
        {
            container = container.copy();
            container.func_190920_e(1);
            IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(container);
            if (fluidHandler != null)
            {
                return fluidHandler.drain(Integer.MAX_VALUE, false);
            }
        }
        return null;
    }

    /**
     * Helper method to get an IFluidHandler for at a block position.
     *
     * Returns null if there is no valid fluid handler.
     */
    @Nullable
    public static IFluidHandler getFluidHandler(World world, BlockPos blockPos, @Nullable EnumFacing side)
    {
        IBlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();

        if (block.hasTileEntity(state))
        {
            TileEntity tileEntity = world.getTileEntity(blockPos);
            if (tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side))
            {
                return tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
            }
        }
        else if (block instanceof IFluidBlock)
        {
            return new FluidBlockWrapper((IFluidBlock) block, world, blockPos);
        }
        else if (block instanceof BlockLiquid)
        {
            return new BlockLiquidWrapper((BlockLiquid) block, world, blockPos);
        }

        return null;
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
    public static FluidActionResult tryPickUpFluid(@Nonnull ItemStack emptyContainer, @Nullable EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side)
    {
        if (emptyContainer.func_190926_b() || worldIn == null || pos == null)
        {
            return FluidActionResult.FAILURE;
        }

        IBlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof IFluidBlock || block instanceof BlockLiquid)
        {
            IFluidHandler targetFluidHandler = FluidUtil.getFluidHandler(worldIn, pos, side);
            if (targetFluidHandler != null)
            {
                FluidActionResult fluidActionResult = FluidUtil.tryFillContainer(emptyContainer, targetFluidHandler, Integer.MAX_VALUE, playerIn, true);
                if (fluidActionResult.isSuccess())
                {
                    return fluidActionResult;
                }
            }
        }
        return FluidActionResult.FAILURE;
    }

    /**
     * Tries to place a fluid in the world in block form and drains the container.
     * Makes a fluid emptying sound when successful.
     * Honors the amount of fluid contained by the used container.
     * Checks if water-like fluids should vaporize like in the nether.
     *
     * Modeled after {@link net.minecraft.item.ItemBucket#tryPlaceContainedLiquid(EntityPlayer, World, BlockPos)}
     *
     * @param player    Player who places the fluid. May be null for blocks like dispensers.
     * @param world     World to place the fluid in
     * @param pos       The position in the world to place the fluid block
     * @param container The fluid container holding the fluidStack to place
     * @param resource  The fluidStack to place
     * @return the container's ItemStack with the remaining amount of fluid if the placement was successful, null otherwise
     */
    @Nonnull
    public static FluidActionResult tryPlaceFluid(@Nullable EntityPlayer player, World world, BlockPos pos, @Nonnull ItemStack container, FluidStack resource)
    {
        if (world == null || resource == null || pos == null)
        {
            return FluidActionResult.FAILURE;
        }

        Fluid fluid = resource.getFluid();
        if (fluid == null || !fluid.canBePlacedInWorld())
        {
            return FluidActionResult.FAILURE;
        }

        // check that we can place the fluid at the destination
        IBlockState destBlockState = world.getBlockState(pos);
        Material destMaterial = destBlockState.getMaterial();
        boolean isDestNonSolid = !destMaterial.isSolid();
        boolean isDestReplaceable = destBlockState.getBlock().isReplaceable(world, pos);
        if (!world.isAirBlock(pos) && !isDestNonSolid && !isDestReplaceable)
        {
            return FluidActionResult.FAILURE; // Non-air, solid, unreplacable block. We can't put fluid here.
        }

        if (world.provider.doesWaterVaporize() && fluid.doesVaporize(resource))
        {
            fluid.vaporize(player, world, pos, resource);
            return tryEmptyContainer(container, new VoidFluidHandler(), Integer.MAX_VALUE, player, true);
        }
        else
        {
            if (!world.isRemote && (isDestNonSolid || isDestReplaceable) && !destMaterial.isLiquid())
            {
                world.destroyBlock(pos, true);
            }

            // Defer the placement to the fluid block
            // Instead of actually "filling", the fluid handler method replaces the block
            Block block = fluid.getBlock();
            IFluidHandler handler;
            if (block instanceof IFluidBlock)
            {
                handler = new FluidBlockWrapper((IFluidBlock) block, world, pos);
            }
            else if (block instanceof BlockLiquid)
            {
                handler = new BlockLiquidWrapper((BlockLiquid) block, world, pos);
            }
            else
            {
                handler = new BlockWrapper(block, world, pos);
            }
            FluidActionResult result = tryEmptyContainer(container, handler, Integer.MAX_VALUE, player, true);
            if (result.isSuccess())
            {
                SoundEvent soundevent = fluid.getEmptySound(resource);
                world.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            return result;
        }
    }
}
