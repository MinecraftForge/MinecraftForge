/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;

/**
 * {@link LivingEquipmentChangeEvent} is fired when the Equipment of a Entity changes. <br>
 * This event is fired whenever changes in Equipment are detected in {@link LivingEntity#tick()}. <br>
 * This also includes entities joining the World, as well as being cloned. <br>
 * This event is fired on server-side only. <br>
 * <br>
 * {@link #slot} contains the affected {@link EquipmentSlot}. <br>
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
    private final EquipmentSlot slot;
    private final ItemStack from;
    private final ItemStack to;

    public LivingEquipmentChangeEvent(LivingEntity entity, EquipmentSlot slot, @NotNull ItemStack from, @NotNull ItemStack to)
    {
        super(entity);
        this.slot = slot;
        this.from = from;
        this.to = to;
    }

    public EquipmentSlot getSlot() { return this.slot; }
    @NotNull
    public ItemStack getFrom() { return this.from; }
    @NotNull
    public ItemStack getTo() { return this.to; }
}
