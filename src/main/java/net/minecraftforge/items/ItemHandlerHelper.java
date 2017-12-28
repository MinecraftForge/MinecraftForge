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

package net.minecraftforge.items;

import com.google.common.collect.Range;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.items.filter.IStackFilter;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ItemHandlerHelper
{
    public static final IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
    public static final EntityEquipmentSlot[] armorSlots = new EntityEquipmentSlot[]{
            EntityEquipmentSlot.FEET, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.HEAD
    };


    public static boolean canItemStacksStack(@Nonnull ItemStack a, @Nonnull ItemStack b)
    {
        if (a.isEmpty() || !a.isItemEqual(b) || a.hasTagCompound() != b.hasTagCompound())
            return false;

        return (!a.hasTagCompound() || a.getTagCompound().equals(b.getTagCompound())) && a.areCapsCompatible(b);
    }

    /**
     * A relaxed version of canItemStacksStack that stacks itemstacks with different metadata if they don't have subtypes.
     * This usually only applies when players pick up items.
     */
    public static boolean canItemStacksStackRelaxed(@Nonnull ItemStack a, @Nonnull ItemStack b)
    {
        if (a.isEmpty() || b.isEmpty() || a.getItem() != b.getItem())
            return false;

        if (!a.isStackable())
            return false;

        // Metadata value only matters when the item has subtypes
        // Vanilla stacks non-subtype items with different metadata together
        // e.g. a stick with metadata 0 and a stick with metadata 1 stack
        if (a.getHasSubtypes() && a.getMetadata() != b.getMetadata())
            return false;

        if (a.hasTagCompound() != b.hasTagCompound())
            return false;

        return (!a.hasTagCompound() || a.getTagCompound().equals(b.getTagCompound())) && a.areCapsCompatible(b);
    }

    @Nonnull
    public static ItemStack copyStackWithSize(@Nonnull ItemStack itemStack, int size)
    {
        if (size == 0)
            return ItemStack.EMPTY;
        ItemStack copy = itemStack.copy();
        copy.setCount(size);
        return copy;
    }

    /**
     * giveItemToPlayer without preferred slot
     */
    public static void giveItemToPlayer(EntityPlayer player, @Nonnull ItemStack stack)
    {
        giveItemToPlayer(player, stack, -1);
    }

    /**
     * Inserts the given itemstack into the players inventory.
     * If the inventory can't hold it, the item will be dropped in the world at the players position.
     *
     * @param player        The player to give the item to
     * @param preferredSlot a slot to try to insert to, if the slot cant accept the stack or preferredSlot is < 0 the stack will be slotLess inserted
     * @param stack         The itemstack to insert
     */
    public static void giveItemToPlayer(EntityPlayer player, @Nonnull ItemStack stack, int preferredSlot)
    {
        IItemHandler inventory = new PlayerMainInvWrapper(player.inventory);
        World world = player.world;

        // try adding it into the inventory
        ItemStack remainder = stack;
        // insert into preferred slot first
        if (preferredSlot >= 0)
        {
            remainder = inventory.insert(Range.singleton(preferredSlot), stack, false).getLeftoverStack();
        }
        // then into the inventory in general
        if (!remainder.isEmpty())
        {
            remainder = inventory.insert(Range.all(), stack, false).getLeftoverStack();
        }

        // play sound if something got picked up
        if (remainder.isEmpty() || remainder.getCount() != stack.getCount())
        {
            world.playSound(player, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }

        // drop remaining itemstack into the world
        if (!remainder.isEmpty() && !world.isRemote)
        {
            EntityItem entityitem = new EntityItem(world, player.posX, player.posY + 0.5, player.posZ, stack);
            entityitem.setPickupDelay(40);
            entityitem.motionX = 0;
            entityitem.motionZ = 0;

            world.spawnEntity(entityitem);
        }
    }

    public static InsertTransaction split(@Nonnull ItemStack stack, int size)
    {
        int i = Math.min(stack.getCount(), size);
        ItemStack insert = copyStackWithSize(stack, i);

        ItemStack leftover = stack.copy();
        leftover.setCount(stack.getCount() - insert.getCount());
        return new InsertTransaction(insert, leftover);
    }

    public static boolean isRangeSlotLess(Range<Integer> range)
    {
        return !range.hasLowerBound() && !range.hasUpperBound();
    }

    public static boolean isRangeSingleton(Range<Integer> range)
    {
        return range.hasLowerBound() && range.hasUpperBound() && Objects.equals(range.lowerEndpoint(), range.upperEndpoint());
    }

    public static int getFreeSpaceForSlot(IItemHandler handler, int slot)
    {
        ItemStack existing = handler.getStackInSlot(slot);
        if (!existing.isEmpty())
        {
            if (!existing.isStackable())
            {
                return 0;
            }
            else return handler.getStackLimit(existing, slot) - existing.getCount();
        }
        return handler.getSlotLimit(slot);
    }

    public static void MultiExtract(IStackFilter filter, Range<Integer> slotRange, IExtractionManager manager, boolean simulate, IItemHandlerModifiable handler)
    {
        if (isRangeSingleton(slotRange))
        {
            int slot = slotRange.lowerEndpoint();
            ItemStack stack = handler.getStackInSlot(slot);
            if (!stack.isEmpty() && handler.canExtractStackFromSlot(stack, slot) && filter.test(stack) && !manager.satisfied())
            {
                int toExtract = manager.extract(stack);

                if (stack.getCount() <= toExtract)
                {
                    if (!simulate)
                    {
                        handler.setStackInSlot(slot, ItemStack.EMPTY);
                    }
                    manager.extractedStack(stack.copy(), slot);
                }
                else
                {
                    if (!simulate)
                    {
                        handler.setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - toExtract));
                    }

                    manager.extractedStack(ItemHandlerHelper.copyStackWithSize(stack, toExtract), slot);
                }
            }
        }
        else
        {
            int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
            int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), handler.size()) : handler.size());
            for (int i = minSlot; i < maxSlot && !manager.satisfied(); i++)
            {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && handler.canExtractStackFromSlot(stack, i) && filter.test(stack))
                {
                    int toExtract = manager.extract(stack);

                    if (stack.getCount() <= toExtract)
                    {
                        if (!simulate)
                        {
                            handler.setStackInSlot(i, ItemStack.EMPTY);
                        }
                        manager.extractedStack(stack.copy(), i);
                    }
                    else
                    {
                        if (!simulate)
                        {
                            handler.setStackInSlot(i, ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - toExtract));
                        }

                        manager.extractedStack(ItemHandlerHelper.copyStackWithSize(stack, toExtract), i);
                    }
                }
            }
        }
    }

    @Nonnull
    public static ItemStack extract(Range<Integer> slotRange, IStackFilter filter, int amount, boolean simulate, IItemHandlerModifiable handler)
    {
        if (amount == 0) return ItemStack.EMPTY;
        if (isRangeSingleton(slotRange))
        {
            return extract(slotRange.lowerEndpoint(), filter, amount, simulate, handler);
        }
        int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
        int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), handler.size()) : handler.size());
        for (int i = minSlot; i < maxSlot; i++)
        {
            ItemStack stack = extract(i, filter, amount, simulate, handler);
            if (!stack.isEmpty())
                return stack;
        }

        return ItemStack.EMPTY;
    }

    @Nonnull
    private static ItemStack extract(int slot, IStackFilter filter, int amount, boolean simulate, IItemHandlerModifiable handler)
    {
        ItemStack existing = handler.getStackInSlot(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        if (!handler.canExtractStackFromSlot(existing, slot))
            return ItemStack.EMPTY;

        if (!filter.test(existing)) return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract)
        {
            if (!simulate)
            {
                handler.setStackInSlot(slot, ItemStack.EMPTY);
                handler.onContentsChanged(slot);
            }
            return existing;
        }
        else
        {
            if (!simulate)
            {
                handler.setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                handler.onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Nonnull
    public static InsertTransaction insert(Range<Integer> slotRange, ItemStack stack, boolean simulate, IItemHandlerModifiable handler)
    {
        if (stack.isEmpty()) return new InsertTransaction(ItemStack.EMPTY, ItemStack.EMPTY);

        if (isRangeSingleton(slotRange))
            return insert(slotRange.lowerEndpoint(), stack, simulate, handler);

        else
        {
            int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
            int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), handler.size()) : handler.size());

            for (int i = minSlot; i < maxSlot; i++)
            {
                InsertTransaction transaction = insert(i, stack, simulate, handler);
                if (!transaction.getInsertedStack().isEmpty())
                {
                    return transaction;
                }
            }
        }
        return new InsertTransaction(ItemStack.EMPTY, stack);
    }

    @Nonnull
    private static InsertTransaction insert(int slot, ItemStack stack, boolean simulate, IItemHandlerModifiable handler)
    {
        ItemStack existing = handler.getStackInSlot(slot);

        int limit = handler.getStackLimit(stack, slot);

        if (!handler.isStackValidForSlot(stack, slot))
            return new InsertTransaction(ItemStack.EMPTY, stack);

        if (!existing.isEmpty())
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return new InsertTransaction(ItemStack.EMPTY, stack);

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return new InsertTransaction(ItemStack.EMPTY, stack);

        InsertTransaction transaction = split(stack, limit);

        if (!simulate)
        {
            if (existing.isEmpty())
            {
                handler.setStackInSlot(slot, transaction.getInsertedStack());
                handler.onContentsChanged(slot);
            }
            else
            {
                existing.grow(transaction.getInsertedStack().getCount());
                handler.onContentsChanged(slot);
            }
        }
        return transaction;
    }
}
