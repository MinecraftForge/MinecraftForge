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

package net.minecraftforge.items;

import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class VanillaInventoryCodeHooks
{
    /**
     * Copied from TileEntityHopper#captureDroppedItems and added capability support
     * @return Null if we did nothing {no IItemHandler}, True if we moved an item, False if we moved no items
     */
    @Nullable
    public static Boolean extractHook(Level level, Hopper dest)
    {
        return getItemHandler(level, dest, Direction.UP)
                .map(itemHandlerResult -> {
                    IItemHandler handler = itemHandlerResult.getKey();

                    for (int i = 0; i < handler.getSlots(); i++)
                    {
                        ItemStack extractItem = handler.extractItem(i, 1, true);
                        if (!extractItem.isEmpty())
                        {
                            for (int j = 0; j < dest.getContainerSize(); j++)
                            {
                                ItemStack destStack = dest.getItem(j);
                                if (dest.canPlaceItem(j, extractItem) && (destStack.isEmpty() || destStack.getCount() < destStack.getMaxStackSize() && destStack.getCount() < dest.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(extractItem, destStack)))
                                {
                                    extractItem = handler.extractItem(i, 1, false);
                                    if (destStack.isEmpty())
                                        dest.setItem(j, extractItem);
                                    else
                                    {
                                        destStack.grow(1);
                                        dest.setItem(j, destStack);
                                    }
                                    dest.setChanged();
                                    return true;
                                }
                            }
                        }
                    }

                    return false;
                })
                .orElse(null); // TODO bad null
    }

    /**
     * Copied from BlockDropper#dispense and added capability support
     */
    public static boolean dropperInsertHook(Level world, BlockPos pos, DispenserBlockEntity dropper, int slot, @Nonnull ItemStack stack)
    {
        Direction enumfacing = world.getBlockState(pos).getValue(DropperBlock.FACING);
        BlockPos blockpos = pos.relative(enumfacing);
        return getItemHandler(world, (double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ(), enumfacing.getOpposite())
                .map(destinationResult -> {
                    IItemHandler itemHandler = destinationResult.getKey();
                    Object destination = destinationResult.getValue();
                    ItemStack dispensedStack = stack.copy().split(1);
                    ItemStack remainder = putStackInInventoryAllSlots(dropper, destination, itemHandler, dispensedStack);

                    if (remainder.isEmpty())
                    {
                        remainder = stack.copy();
                        remainder.shrink(1);
                    }
                    else
                    {
                        remainder = stack.copy();
                    }

                    dropper.setItem(slot, remainder);
                    return false;
                })
                .orElse(true);
    }

    /**
     * Copied from TileEntityHopper#transferItemsOut and added capability support
     */
    public static boolean insertHook(HopperBlockEntity hopper)
    {
        Direction hopperFacing = hopper.getBlockState().getValue(HopperBlock.FACING);
        return getItemHandler(hopper.getLevel(), hopper, hopperFacing)
                .map(destinationResult -> {
                    IItemHandler itemHandler = destinationResult.getKey();
                    Object destination = destinationResult.getValue();
                    if (isFull(itemHandler))
                    {
                        return false;
                    }
                    else
                    {
                        for (int i = 0; i < hopper.getContainerSize(); ++i)
                        {
                            if (!hopper.getItem(i).isEmpty())
                            {
                                ItemStack originalSlotContents = hopper.getItem(i).copy();
                                ItemStack insertStack = hopper.removeItem(i, 1);
                                ItemStack remainder = putStackInInventoryAllSlots(hopper, destination, itemHandler, insertStack);

                                if (remainder.isEmpty())
                                {
                                    return true;
                                }

                                hopper.setItem(i, originalSlotContents);
                            }
                        }

                        return false;
                    }
                })
                .orElse(false);
    }

    private static ItemStack putStackInInventoryAllSlots(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack)
    {
        for (int slot = 0; slot < destInventory.getSlots() && !stack.isEmpty(); slot++)
        {
            stack = insertStack(source, destination, destInventory, stack, slot);
        }
        return stack;
    }

    /**
     * Copied from TileEntityHopper#insertStack and added capability support
     */
    private static ItemStack insertStack(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack, int slot)
    {
        ItemStack itemstack = destInventory.getStackInSlot(slot);

        if (destInventory.insertItem(slot, stack, true).isEmpty())
        {
            boolean insertedItem = false;
            boolean inventoryWasEmpty = isEmpty(destInventory);

            if (itemstack.isEmpty())
            {
                destInventory.insertItem(slot, stack, false);
                stack = ItemStack.EMPTY;
                insertedItem = true;
            }
            else if (ItemHandlerHelper.canItemStacksStack(itemstack, stack))
            {
                int originalSize = stack.getCount();
                stack = destInventory.insertItem(slot, stack, false);
                insertedItem = originalSize < stack.getCount();
            }

            if (insertedItem)
            {
                if (inventoryWasEmpty && destination instanceof HopperBlockEntity)
                {
                    HopperBlockEntity destinationHopper = (HopperBlockEntity)destination;

                    if (!destinationHopper.isOnCustomCooldown())
                    {
                        int k = 0;
                        if (source instanceof HopperBlockEntity)
                        {
                            if (destinationHopper.getLastUpdateTime() >= ((HopperBlockEntity) source).getLastUpdateTime())
                            {
                                k = 1;
                            }
                        }
                        destinationHopper.setCooldown(8 - k);
                    }
                }
            }
        }

        return stack;
    }

    private static Optional<Pair<IItemHandler, Object>> getItemHandler(Level level, Hopper hopper, Direction hopperFacing)
    {
        double x = hopper.getLevelX() + (double) hopperFacing.getStepX();
        double y = hopper.getLevelY() + (double) hopperFacing.getStepY();
        double z = hopper.getLevelZ() + (double) hopperFacing.getStepZ();
        return getItemHandler(level, x, y, z, hopperFacing.getOpposite());
    }

    private static boolean isFull(IItemHandler itemHandler)
    {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++)
        {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.isEmpty() || stackInSlot.getCount() < itemHandler.getSlotLimit(slot))
            {
                return false;
            }
        }
        return true;
    }

    private static boolean isEmpty(IItemHandler itemHandler)
    {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++)
        {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.getCount() > 0)
            {
                return false;
            }
        }
        return true;
    }

    public static Optional<Pair<IItemHandler, Object>> getItemHandler(Level worldIn, double x, double y, double z, final Direction side)
    {
        int i = Mth.floor(x);
        int j = Mth.floor(y);
        int k = Mth.floor(z);
        BlockPos blockpos = new BlockPos(i, j, k);
        net.minecraft.world.level.block.state.BlockState state = worldIn.getBlockState(blockpos);

        if (state.hasBlockEntity())
        {
            BlockEntity blockEntity = worldIn.getBlockEntity(blockpos);
            if (blockEntity != null)
            {
                return blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)
                    .map(capability -> ImmutablePair.<IItemHandler, Object>of(capability, blockEntity));
            }
        }

        return Optional.empty();
    }
}
