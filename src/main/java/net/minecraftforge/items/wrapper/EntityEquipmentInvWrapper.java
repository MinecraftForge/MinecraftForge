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

package net.minecraftforge.items.wrapper;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.IItemHandlerObserver;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.filter.IStackFilter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;

/**
 * Exposes the armor or hands inventory of an {@link EntityLivingBase} as an {@link IItemHandler} using {@link EntityLivingBase#getItemStackFromSlot} and
 * {@link EntityLivingBase#setItemStackToSlot}.
 */
public abstract class EntityEquipmentInvWrapper implements IItemHandlerModifiable
{
    List<IItemHandlerObserver> observers = new ArrayList<>();

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
    public int size()
    {
        return slots.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(final int slot)
    {
        return entity.getItemStackFromSlot(validateSlotIndex(slot));
    }

    protected EntityEquipmentSlot validateSlotIndex(final int slot)
    {
        if (slot < 0 || slot >= slots.size())
            throw new IllegalArgumentException("Slot " + slot + " not in valid range - [0," + slots.size() + ")");

        return slots.get(slot);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 64;
    }

    @Override
    public void clearInv()
    {
        for (EntityEquipmentSlot equipmentSlot : EntityEquipmentSlot.values())
            entity.setItemStackToSlot(equipmentSlot, ItemStack.EMPTY);
    }

    @Override
    @Nonnull
    public ItemStack setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        ItemStack stack1 = entity.getItemStackFromSlot(validateSlotIndex(slot));
        entity.setItemStackToSlot(validateSlotIndex(slot), stack);
        return stack1;
    }

    @Nonnull
    @Override
    public ItemStack insert(OptionalInt slot, @Nonnull ItemStack stack, boolean simulate)
    {
        return ItemHandlerHelper.insert(slot, stack, simulate, this, observers);
    }

    @Nonnull
    @Override
    public ItemStack extract(OptionalInt slot, IStackFilter filter, int amount, boolean simulate)
    {
        return ItemHandlerHelper.extract(slot, filter, amount, simulate, this, observers);
    }

    @Override
    public void addObserver(IItemHandlerObserver observer)
    {
        observers.add(observer);
    }

    @Override
    public void removeObserver(IItemHandlerObserver observer)
    {
        observers.remove(observer);
    }

}
