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
import java.util.OptionalInt;

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
            remainder = inventory.insert(OptionalInt.of(preferredSlot), stack, false);
        }
        // then into the inventory in general
        if (!remainder.isEmpty())
        {
            remainder = inventory.insert(OptionalInt.empty(), stack, false);
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

    @Nonnull
    public static ItemStack extract(OptionalInt slot, IStackFilter filter, int amount, boolean simlate, IItemHandlerModifiable handler)
    {
        if (amount == 0) return ItemStack.EMPTY;
        if (slot.isPresent())
            return extract(slot.getAsInt(), filter, amount, simlate, handler);
        for (int i = 0; i < handler.size(); i++)
        {
            ItemStack extract = extract(i, filter, amount, simlate, handler);
            if (!extract.isEmpty())
                return extract;
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    private static ItemStack extract(int slot, IStackFilter filter, int amount, boolean simulate, IItemHandlerModifiable handler)
    {

        ItemStack existing = handler.getStackInSlot(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        if (handler.canExtractStackFromSlot(slot, existing) && !filter.test(existing))
            return ItemStack.EMPTY;

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
    public static ItemStack insert(OptionalInt slot, @Nonnull ItemStack stack, boolean simulate, IItemHandlerModifiable handler)
    {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (slot.isPresent())
            return insert(slot.getAsInt(), stack, simulate, handler);
        for (int i = 0; i < handler.size(); i++)
        {
            ItemStack remainder = insert(i, stack, simulate, handler);
            if (remainder.getCount() != stack.getCount())
                return remainder;
        }
        return stack;
    }

    @Nonnull
    private static ItemStack insert(int slot, @Nonnull ItemStack stack, boolean simulate, IItemHandlerModifiable handler)
    {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!handler.isStackValidForSlot(slot, stack))
            return stack;

        ItemStack existing = handler.getStackInSlot(slot);

        int limit = handler.getStackLimit(stack, slot);

        if (!existing.isEmpty())
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate)
        {
            if (existing.isEmpty())
            {
                handler.setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else
            {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            handler.onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }
}
