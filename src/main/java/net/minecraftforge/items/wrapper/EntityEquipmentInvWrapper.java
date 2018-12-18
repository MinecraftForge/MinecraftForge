/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.items.wrapper;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.OptionalCapabilityInstance;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Exposes the armor or hands inventory of an {@link EntityLivingBase} as an {@link IItemHandler} using {@link EntityLivingBase#getItemStackFromSlot} and
 * {@link EntityLivingBase#setItemStackToSlot}.
 */
public abstract class EntityEquipmentInvWrapper implements IItemHandlerModifiable
{
    /**
     * The entity.
     */
    protected final EntityLivingBase entity;

    /**
     * The slots exposed by this wrapper, with {@link EntityEquipmentSlot#index} as the index.
     */
    protected final List<EntityEquipmentSlot> slots;

    /**
     * @param entity   The entity.
     * @param slotType The slot type to expose.
     */
    public EntityEquipmentInvWrapper(final EntityLivingBase entity, final EntityEquipmentSlot.Type slotType)
    {
        this.entity = entity;

        final List<EntityEquipmentSlot> slots = new ArrayList<EntityEquipmentSlot>();

        for (final EntityEquipmentSlot slot : EntityEquipmentSlot.values())
        {
            if (slot.getSlotType() == slotType)
            {
                slots.add(slot);
            }
        }

        this.slots = ImmutableList.copyOf(slots);
    }

    @Override
    public int getSlots()
    {
        return slots.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(final int slot)
    {
        return entity.getItemStackFromSlot(validateSlotIndex(slot));
    }

    @Nonnull
    @Override
    public ItemStack insertItem(final int slot, @Nonnull final ItemStack stack, final boolean simulate)
    {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        final EntityEquipmentSlot equipmentSlot = validateSlotIndex(slot);

        final ItemStack existing = entity.getItemStackFromSlot(equipmentSlot);

        int limit = getStackLimit(slot, stack);

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
                entity.setItemStackToSlot(equipmentSlot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else
            {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate)
    {
        if (amount == 0)
            return ItemStack.EMPTY;

        final EntityEquipmentSlot equipmentSlot = validateSlotIndex(slot);

        final ItemStack existing = entity.getItemStackFromSlot(equipmentSlot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        final int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract)
        {
            if (!simulate)
            {
                entity.setItemStackToSlot(equipmentSlot, ItemStack.EMPTY);
            }

            return existing;
        }
        else
        {
            if (!simulate)
            {
                entity.setItemStackToSlot(equipmentSlot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(final int slot)
    {
        final EntityEquipmentSlot equipmentSlot = validateSlotIndex(slot);
        return equipmentSlot.getSlotType() == EntityEquipmentSlot.Type.ARMOR ? 1 : 64;
    }

    protected int getStackLimit(final int slot, @Nonnull final ItemStack stack)
    {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public void setStackInSlot(final int slot, @Nonnull final ItemStack stack)
    {
        final EntityEquipmentSlot equipmentSlot = validateSlotIndex(slot);
        if (ItemStack.areItemStacksEqual(entity.getItemStackFromSlot(equipmentSlot), stack))
            return;
        entity.setItemStackToSlot(equipmentSlot, stack);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        return IItemHandlerModifiable.super.isItemValid(slot, stack);
    }

    protected EntityEquipmentSlot validateSlotIndex(final int slot)
    {
        if (slot < 0 || slot >= slots.size())
            throw new IllegalArgumentException("Slot " + slot + " not in valid range - [0," + slots.size() + ")");

        return slots.get(slot);
    }

    public static OptionalCapabilityInstance<IItemHandlerModifiable>[] create(EntityLivingBase entity)
    {
        @SuppressWarnings("unchecked")
        OptionalCapabilityInstance<IItemHandlerModifiable>[] ret = new OptionalCapabilityInstance[3];
        ret[0] = OptionalCapabilityInstance.of(() -> new EntityHandsInvWrapper(entity));
        ret[1] = OptionalCapabilityInstance.of(() -> new EntityArmorInvWrapper(entity));
        ret[2] = OptionalCapabilityInstance.of(() -> new CombinedInvWrapper(ret[0].orElse(null), ret[1].orElse(null)));
        return ret;
    }
}
