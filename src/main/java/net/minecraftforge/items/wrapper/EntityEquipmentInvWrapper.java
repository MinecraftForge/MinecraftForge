/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.wrapper;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;


/**
 * Exposes the armor or hands inventory of an {@link LivingEntity} as an {@link IItemHandler} using {@link LivingEntity#getItemBySlot(EquipmentSlot)} and
 * {@link LivingEntity#setItemSlot(EquipmentSlot, ItemStack)}.
 */
public abstract class EntityEquipmentInvWrapper implements IItemHandlerModifiable
{
    /**
     * The entity.
     */
    protected final LivingEntity entity;

    /**
     * The slots exposed by this wrapper, with {@link EquipmentSlot#getIndex()} as the index.
     */
    protected final List<EquipmentSlot> slots;

    /**
     * @param entity   The entity.
     * @param slotType The slot type to expose.
     */
    public EntityEquipmentInvWrapper(final LivingEntity entity, final EquipmentSlot.Type slotType)
    {
        this.entity = entity;

        final List<EquipmentSlot> slots = new ArrayList<EquipmentSlot>();

        for (final EquipmentSlot slot : EquipmentSlot.values())
        {
            if (slot.getType() == slotType)
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
        return entity.getItemBySlot(validateSlotIndex(slot));
    }

    @Nonnull
    @Override
    public ItemStack insertItem(final int slot, @Nonnull final ItemStack stack, final boolean simulate)
    {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        final EquipmentSlot equipmentSlot = validateSlotIndex(slot);

        final ItemStack existing = entity.getItemBySlot(equipmentSlot);

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
                entity.setItemSlot(equipmentSlot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
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

        final EquipmentSlot equipmentSlot = validateSlotIndex(slot);

        final ItemStack existing = entity.getItemBySlot(equipmentSlot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        final int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract)
        {
            if (!simulate)
            {
                entity.setItemSlot(equipmentSlot, ItemStack.EMPTY);
            }

            return existing;
        }
        else
        {
            if (!simulate)
            {
                entity.setItemSlot(equipmentSlot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(final int slot)
    {
        final EquipmentSlot equipmentSlot = validateSlotIndex(slot);
        return equipmentSlot.getType() == EquipmentSlot.Type.ARMOR ? 1 : 64;
    }

    protected int getStackLimit(final int slot, @Nonnull final ItemStack stack)
    {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public void setStackInSlot(final int slot, @Nonnull final ItemStack stack)
    {
        final EquipmentSlot equipmentSlot = validateSlotIndex(slot);
        if (ItemStack.matches(entity.getItemBySlot(equipmentSlot), stack))
            return;
        entity.setItemSlot(equipmentSlot, stack);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        return true;
    }

    protected EquipmentSlot validateSlotIndex(final int slot)
    {
        if (slot < 0 || slot >= slots.size())
            throw new IllegalArgumentException("Slot " + slot + " not in valid range - [0," + slots.size() + ")");

        return slots.get(slot);
    }

    public static LazyOptional<IItemHandlerModifiable>[] create(LivingEntity entity)
    {
        @SuppressWarnings("unchecked")
        LazyOptional<IItemHandlerModifiable>[] ret = new LazyOptional[3];
        ret[0] = LazyOptional.of(() -> new EntityHandsInvWrapper(entity));
        ret[1] = LazyOptional.of(() -> new EntityArmorInvWrapper(entity));
        ret[2] = LazyOptional.of(() -> new CombinedInvWrapper(ret[0].orElse(null), ret[1].orElse(null)));
        return ret;
    }
}
