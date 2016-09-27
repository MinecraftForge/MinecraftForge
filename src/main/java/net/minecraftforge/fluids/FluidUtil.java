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
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
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
     * Returns true if interaction was successful.
     * Returns false if interaction failed.
     */
    public static boolean interactWithFluidHandler(ItemStack stack, IFluidHandler fluidHandler, EntityPlayer player)
    {
        if (stack == null || fluidHandler == null || player == null)
        {
            return false;
        }

        IItemHandler playerInventory = new InvWrapper(player.inventory);
        return tryFillContainerAndStow(stack, fluidHandler, playerInventory, Integer.MAX_VALUE, player) ||
                tryEmptyContainerAndStow(stack, fluidHandler, playerInventory, Integer.MAX_VALUE, player);
    }

    /**
     * Fill a container from the given fluidSource.
     *
     * @param container   The container to be filled. Will not be modified.
     * @param fluidSource The fluid handler to be drained.
     * @param maxAmount   The largest amount of fluid that should be transferred.
     * @param player      The player to make the filling noise. Pass null for no noise.
     * @param doFill      true if the container should actually be filled, false if it should be simulated.
     * @return The filled container or null if the liquid couldn't be taken from the tank.
     */
    public static ItemStack tryFillContainer(ItemStack container, IFluidHandler fluidSource, int maxAmount, @Nullable EntityPlayer player, boolean doFill)
    {
        container = container.copy(); // do not modify the input
        container.stackSize = 1;
        IFluidHandler containerFluidHandler = getFluidHandler(container);
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
                return container;
            }
        }
        return null;
    }

    /**
     * Takes a filled container and tries to empty it into the given tank.
     *
     * @param container        The filled container. Will not be modified.
     * @param fluidDestination The fluid handler to be filled by the container.
     * @param maxAmount        The largest amount of fluid that should be transferred.
     * @param player           Player for making the bucket drained sound. Pass null for no noise.
     * @param doDrain          true if the container should actually be drained, false if it should be simulated.
     * @return The empty container if successful, null if the fluid handler couldn't be filled.
     *         NOTE The empty container will have a stackSize of 0 when a filled container is consumable,
     *              i.e. it has a "null" empty container but has successfully been emptied.
     */
    @Nullable
    public static ItemStack tryEmptyContainer(ItemStack container, IFluidHandler fluidDestination, int maxAmount, @Nullable EntityPlayer player, boolean doDrain)
    {
        container = container.copy(); // do not modify the input
        container.stackSize = 1;
        IFluidHandler containerFluidHandler = getFluidHandler(container);
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
                return container;
            }
        }
        return null;
    }

    /**
     * Takes an Fluid Container Item and tries to fill it from the given tank.
     * If the player is in creative mode, the container will not be modified on success, and no additional items created.
     * If the input itemstack has a stacksize > 1 it will stow the filled container in the given inventory.
     * If the inventory does not accept it, it will be given to the player or dropped at the players feet.
     *      If player is null in this case, the action will be aborted.
     *
     * @param container   The Fluid Container Itemstack to fill. This stack WILL be modified on success.
     * @param fluidSource The fluid source to fill from
     * @param inventory   An inventory where any additionally created item (filled container if multiple empty are present) are put
     * @param maxAmount   Maximum amount of fluid to take from the tank.
     * @param player      The player that gets the items the inventory can't take. Can be null, only used if the inventory cannot take the filled stack.
     * @return True if the container was filled successfully and stowed, false otherwise.
     */
    public static boolean tryFillContainerAndStow(ItemStack container, IFluidHandler fluidSource, IItemHandler inventory, int maxAmount, @Nullable EntityPlayer player)
    {
        if (container == null || container.stackSize < 1)
        {
            return false;
        }

        if (player != null && player.capabilities.isCreativeMode)
        {
            ItemStack filledReal = tryFillContainer(container, fluidSource, maxAmount, player, true);
            if (filledReal != null)
            {
                return true;
            }
        }
        else if (container.stackSize == 1) // don't need to stow anything, just fill and edit the container stack
        {
            ItemStack filledReal = tryFillContainer(container, fluidSource, maxAmount, player, true);
            if (filledReal != null)
            {
                container.deserializeNBT(filledReal.serializeNBT());
                return true;
            }
        }
        else
        {
            ItemStack filledSimulated = tryFillContainer(container, fluidSource, maxAmount, player, false);
            if (filledSimulated != null)
            {
                // check if we can give the itemStack to the inventory
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, filledSimulated, true);
                if (remainder == null || player != null)
                {
                    ItemStack filledReal = tryFillContainer(container, fluidSource, maxAmount, player, true);
                    remainder = ItemHandlerHelper.insertItemStacked(inventory, filledReal, false);

                    // give it to the player or drop it at their feet
                    if (remainder != null && player != null)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, remainder);
                    }

                    container.stackSize--;
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Takes an Fluid Container Item, tries to empty it into the fluid handler, and stows it in the given inventory.
     * If the player is in creative mode, the container will not be modified on success, and no additional items created.
     * If the input itemstack has a stacksize > 1 it will stow the emptied container in the given inventory.
     * If the inventory does not accept the emptied container, it will be given to the player or dropped at the players feet.
     *      If player is null in this case, the action will be aborted.
     *
     * @param container        The filled Fluid Container Itemstack to empty. This stack WILL be modified on success.
     * @param fluidDestination The fluid destination to fill from the fluid container.
     * @param inventory        An inventory where any additionally created item (filled container if multiple empty are present) are put
     * @param maxAmount        Maximum amount of fluid to take from the tank.
     * @param player           The player that gets the items the inventory can't take. Can be null, only used if the inventory cannot take the filled stack.
     * @return True if the container was filled successfully and stowed, false otherwise.
     */
    public static boolean tryEmptyContainerAndStow(ItemStack container, IFluidHandler fluidDestination, IItemHandler inventory, int maxAmount, @Nullable EntityPlayer player)
    {
        if (container == null || container.stackSize < 1)
        {
            return false;
        }

        if (player != null && player.capabilities.isCreativeMode)
        {
            ItemStack emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, true);
            if (emptiedReal != null)
            {
                return true;
            }
        }
        else if (container.stackSize == 1) // don't need to stow anything, just fill and edit the container stack
        {
            ItemStack emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, true);
            if (emptiedReal != null)
            {
                if (emptiedReal.stackSize <= 0)
                {
                    container.stackSize--;
                }
                else
                {
                    container.deserializeNBT(emptiedReal.serializeNBT());
                }
                return true;
            }
        }
        else
        {
            ItemStack emptiedSimulated = tryEmptyContainer(container, fluidDestination, maxAmount, player, false);
            if (emptiedSimulated != null)
            {
                if (emptiedSimulated.stackSize <= 0)
                {
                    tryEmptyContainer(container, fluidDestination, maxAmount, player, true);
                    container.stackSize--;
                    return true;
                }
                else
                {
                    // check if we can give the itemStack to the inventory
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedSimulated, true);
                    if (remainder == null || player != null)
                    {
                        ItemStack emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, true);
                        remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedReal, false);

                        // give it to the player or drop it at their feet
                        if (remainder != null && player != null)
                        {
                            ItemHandlerHelper.giveItemToPlayer(player, remainder);
                        }

                        container.stackSize--;
                        return true;
                    }
                }
            }
        }

        return false;
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
                FluidStack drained = fluidSource.drain(fillableAmount, doTransfer);
                if (drained != null)
                {
                    drained.amount = fluidDestination.fill(drained, doTransfer);
                    return drained;
                }
            }
        }
        return null;
    }

    /**
     * Helper method to get an IFluidHandler for an itemStack.
     *
     * The itemStack passed in here WILL be modified, the IFluidHandler acts on it directly.
     *
     * Note that the itemStack MUST have a stackSize of 1 if you want to fill or drain it.
     * You can't fill or drain a whole stack at once, if you do then liquid is multiplied or destroyed.
     *
     * Vanilla buckets will be converted to universal buckets if they are enabled.
     *
     * Returns null if the itemStack passed in does not have a fluid handler.
     */
    @Nullable
    public static IFluidHandler getFluidHandler(ItemStack itemStack)
    {
        if (itemStack != null && itemStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
        {
            return itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
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
    public static FluidStack getFluidContained(ItemStack container)
    {
        if (container != null)
        {
            container = container.copy();
            container.stackSize = 1;
            IFluidHandler fluidHandler = FluidUtil.getFluidHandler(container);
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
     * Tries to place a fluid in the world in block form.
     * Makes a fluid emptying sound when successful.
     * Checks if water-like fluids should vaporize like in the nether.
     *
     * Modeled after {@link net.minecraft.item.ItemBucket#tryPlaceContainedLiquid(EntityPlayer, World, BlockPos)}
     *
     * @param player     Player who places the fluid. May be null for blocks like dispensers.
     * @param worldIn    World to place the fluid in
     * @param fluidStack The fluidStack to place.
     * @param pos        The position in the world to place the fluid block
     * @return true if successful
     */
    public static boolean tryPlaceFluid(@Nullable EntityPlayer player, World worldIn, FluidStack fluidStack, BlockPos pos)
    {
        if (worldIn == null || fluidStack == null || pos == null)
        {
            return false;
        }

        Fluid fluid = fluidStack.getFluid();
        if (fluid == null || !fluid.canBePlacedInWorld())
        {
            return false;
        }

        // check that we can place the fluid at the destination
        IBlockState destBlockState = worldIn.getBlockState(pos);
        Material destMaterial = destBlockState.getMaterial();
        boolean isDestNonSolid = !destMaterial.isSolid();
        boolean isDestReplaceable = destBlockState.getBlock().isReplaceable(worldIn, pos);
        if (!worldIn.isAirBlock(pos) && !isDestNonSolid && !isDestReplaceable)
        {
            return false; // Non-air, solid, unreplacable block. We can't put fluid here.
        }

        if (worldIn.provider.doesWaterVaporize() && fluid.doesVaporize(fluidStack))
        {
            fluid.vaporize(player, worldIn, pos, fluidStack);
        }
        else
        {
            if (!worldIn.isRemote && (isDestNonSolid || isDestReplaceable) && !destMaterial.isLiquid())
            {
                worldIn.destroyBlock(pos, true);
            }

            SoundEvent soundevent = fluid.getEmptySound(fluidStack);
            worldIn.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);

            IBlockState fluidBlockState = fluid.getBlock().getDefaultState();
            worldIn.setBlockState(pos, fluidBlockState, 11);
        }
        return true;
    }

    /**
     * Attempts to pick up a fluid in the world and put it in an empty container item.
     *
     * @param emptyContainer The empty container to fill. Will not be modified.
     * @param playerIn       The player filling the container. Optional.
     * @param worldIn        The world the fluid is in.
     * @param pos            The position of the fluid in the world.
     * @param side           The side of the fluid that is being drained.
     * @return a filled container if it was successful. returns null on failure.
     */
    @Nullable
    public static ItemStack tryPickUpFluid(ItemStack emptyContainer, @Nullable EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side)
    {
        if (emptyContainer == null || worldIn == null || pos == null) {
            return null;
        }

        IBlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof IFluidBlock || block instanceof BlockLiquid)
        {
            IFluidHandler targetFluidHandler = FluidUtil.getFluidHandler(worldIn, pos, side);
            if (targetFluidHandler != null)
            {
                return FluidUtil.tryFillContainer(emptyContainer, targetFluidHandler, Integer.MAX_VALUE, playerIn, true);
            }
        }
        return null;
    }
}
