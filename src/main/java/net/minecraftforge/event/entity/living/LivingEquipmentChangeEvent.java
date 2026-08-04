/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nonnull;

/**
 * {@link LivingEquipmentChangeEvent} is fired when the Equipment of a Entity changes. <br>
 * This event is fired whenever changes in Equipment are detected in {@link EntityLivingBase#onUpdate()}. <br>
 * This also includes entities joining the World, as well as being cloned. <br>
 * This event is fired on server-side only. <br>
 * <br>
 * {@link #slot} contains the affected {@link EntityEquipmentSlot}. <br>
 * {@link #from} contains the {@link ItemStack} that was equipped previously. <br>
 * {@link #to} contains the {@link ItemStack} that is equipped now. <br>
 * <br>
 * This event is not {@link Cancelable}. <br>
 * <br>
 * This event does not have a result. {@link HasResult} <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class LivingEquipmentChangeEvent extends LivingEvent
{
    private final EquipmentSlotType slot;
    private final ItemStack from;
    private final ItemStack to;

    public LivingEquipmentChangeEvent(LivingEntity entity, EquipmentSlotType slot, @Nonnull ItemStack from, @Nonnull ItemStack to)
    {
        super(entity);
        this.slot = slot;
        this.from = from;
        this.to = to;
    }

    public EquipmentSlotType getSlot() { return this.slot; }
    @Nonnull
    public ItemStack getFrom() { return this.from; }
    @Nonnull
    public ItemStack getTo() { return this.to; }
}
