/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items;

import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

//TODO: [Forge][1.21.9][Cleanup] Mark as @ApiStatus.Internal
public class VanillaInventoryCodeHooks {
    /**
     * Copied from HopperBlockEntity#suckInItems and added capability support
     * @return Null if we did nothing {no IItemHandler}, True if we moved an item, False if we moved no items
     */
    @Nullable
    public static Boolean extractHook(Level level, Hopper dest) {
        var handler = getItemHandler(level, dest, Direction.UP).map(Pair::getKey).orElse(null);
        if (handler == null)
            return null;

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack extractItem = handler.extractItem(i, 1, true);
            if (!extractItem.isEmpty()) {
                for (int j = 0; j < dest.getContainerSize(); j++) {
                    ItemStack destStack = dest.getItem(j);
                    if (dest.canPlaceItem(j, extractItem) && (destStack.isEmpty() || destStack.getCount() < destStack.getMaxStackSize() && destStack.getCount() < dest.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(extractItem, destStack))) {
                        extractItem = handler.extractItem(i, 1, false);
                        if (destStack.isEmpty())
                            dest.setItem(j, extractItem);
                        else {
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
    }

    /**
     * Copied from DropperBlock#dispenseFrom and added capability support
     */
    public static boolean dropperInsertHook(Level level, BlockPos pos, DispenserBlockEntity dropper, int slot, @NotNull ItemStack stack) {
        var direction = level.getBlockState(pos).getValue(DropperBlock.FACING);
        var handler = getItemHandlerPair(level, pos.relative(direction), direction.getOpposite());
        if (handler.isEmpty())
            return true;

        var itemHandler = handler.get().getKey();
        var destination = handler.get().getValue();

        ItemStack dispensedStack = stack.copy().split(1);
        ItemStack remainder = putStackInInventoryAllSlots(dropper, destination, itemHandler, dispensedStack);

        if (remainder.isEmpty()) {
            remainder = stack.copy();
            remainder.shrink(1);
        } else
            remainder = stack.copy();

        dropper.setItem(slot, remainder);
        return false;
    }

    /**
     * Copied from HopperBlockEntity#ejectItems and added capability support
     */
    public static boolean insertHook(HopperBlockEntity hopper) {
        var handler = getItemHandler(hopper.getLevel(), hopper, hopper.getBlockState().getValue(HopperBlock.FACING));
        if (handler.isEmpty())
            return false;

        var itemHandler = handler.get().getKey();
        var destination = handler.get().getValue();

        if (isFull(itemHandler))
            return false;

        for (int i = 0; i < hopper.getContainerSize(); ++i) {
            if (!hopper.getItem(i).isEmpty()) {
                ItemStack originalSlotContents = hopper.getItem(i).copy();
                ItemStack insertStack = hopper.removeItem(i, 1);
                ItemStack remainder = putStackInInventoryAllSlots(hopper, destination, itemHandler, insertStack);

                if (remainder.isEmpty())
                    return true;

                hopper.setItem(i, originalSlotContents);
            }
        }

        return false;
    }

    private static ItemStack putStackInInventoryAllSlots(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack) {
        for (int slot = 0; slot < destInventory.getSlots() && !stack.isEmpty(); slot++)
            stack = insertStack(source, destination, destInventory, stack, slot);
        return stack;
    }

    /**
     * Copied from HopperBlockEntity#tryMoveInItem and added capability support
     */
    private static ItemStack insertStack(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack, int slot) {
        ItemStack itemstack = destInventory.getStackInSlot(slot);

        if (destInventory.insertItem(slot, stack, true).isEmpty()) {
            boolean insertedItem = false;
            boolean inventoryWasEmpty = isEmpty(destInventory);

            if (itemstack.isEmpty()) {
                destInventory.insertItem(slot, stack, false);
                stack = ItemStack.EMPTY;
                insertedItem = true;
            } else if (ItemHandlerHelper.canItemStacksStack(itemstack, stack)) {
                int originalSize = stack.getCount();
                stack = destInventory.insertItem(slot, stack, false);
                insertedItem = originalSize < stack.getCount();
            }

            if (insertedItem) {
                if (inventoryWasEmpty && destination instanceof HopperBlockEntity dHopper) {
                    if (!dHopper.isOnCustomCooldown()) {
                        int k = 0;
                        if (source instanceof HopperBlockEntity sHopper && dHopper.getLastUpdateTime() >= sHopper.getLastUpdateTime())
                            k = 1;
                        dHopper.setCooldown(8 - k);
                    }
                }
            }
        }

        return stack;
    }

    private static Optional<Pair<IItemHandler, Object>> getItemHandler(Level level, Hopper hopper, Direction hopperFacing) {
        double x = hopper.getLevelX() + (double) hopperFacing.getStepX();
        double y = hopper.getLevelY() + (double) hopperFacing.getStepY();
        double z = hopper.getLevelZ() + (double) hopperFacing.getStepZ();
        return getItemHandler(level, x, y, z, hopperFacing.getOpposite());
    }

    private static boolean isFull(IItemHandler itemHandler) {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.isEmpty() || stackInSlot.getCount() < itemHandler.getSlotLimit(slot))
                return false;
        }
        return true;
    }

    private static boolean isEmpty(IItemHandler itemHandler) {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.getCount() > 0)
                return false;
        }
        return true;
    }

    /**
     * Gets the IItemHandler at the given position, checking for both BlockEntities
     * and Entities. If both are present, the BlockEntity is preferred.
     *
     * This is equivalent to HopperBlockEntity#getContainerAt
     *
     * @param level The level to check
     * @param pos   The position to check
     * @param side  The side to check from. Can be null.
     * @return An IItemHandler and the object it was obtained from, or an empty
     *         Optional if none was found.
     */
    public static Optional<IItemHandler> getItemHandler(Level level, BlockPos pos, @Nullable Direction side) {
        var handler = getItemHandlerBlock(level, pos, side);
        if (!handler.isPresent())
            handler = getItemHandlerEntity(level, pos, side);
        return handler;
    }

    public static Optional<IItemHandler> getItemHandlerEntity(Level level, BlockPos pos, @Nullable Direction side) {
        var entities = level.getEntities((Entity)null, new AABB(pos), e -> e.getCapability(ForgeCapabilities.ITEM_HANDLER, side).isPresent());

        if (entities.isEmpty())
            return Optional.empty();

        var rand = level.random.nextInt(entities.size());
        var entity = entities.get(rand);
        return entity.getCapability(ForgeCapabilities.ITEM_HANDLER, side).resolve();
    }

    public static Optional<IItemHandler> getItemHandlerBlock(Level level, BlockPos pos, @Nullable Direction side) {
        if (!level.getBlockState(pos).hasBlockEntity())
            return Optional.empty();

        var entity = level.getBlockEntity(pos);
        if (entity == null)
            return Optional.empty();

        return entity.getCapability(ForgeCapabilities.ITEM_HANDLER, side).resolve();
    }

    /**
     *  I don't think anyone should be using this. If they are they probably want the non-pair version.
     *  As the only use for knowing the object holding the IItemHandler is for hopper cooldowns
     */
    @Deprecated(forRemoval = true, since = "1.21.8")
    public static Optional<Pair<IItemHandler, Object>> getItemHandler(Level worldIn, double x, double y, double z, final Direction side) {
        int i = Mth.floor(x);
        int j = Mth.floor(y);
        int k = Mth.floor(z);
        return getItemHandlerPair(worldIn, new BlockPos(i, j, k), side);
    }

    private static Optional<Pair<IItemHandler, Object>> getItemHandlerPair(Level level, BlockPos pos, Direction side) {
        var state = level.getBlockState(pos);

        if (state.hasBlockEntity()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                return blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, side)
                    .map(capability -> ImmutablePair.<IItemHandler, Object>of(capability, blockEntity));
            }
        }

        return Optional.empty();
    }
}
