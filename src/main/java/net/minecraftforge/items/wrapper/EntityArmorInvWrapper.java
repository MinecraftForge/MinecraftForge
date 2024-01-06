/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.wrapper;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * Exposes the armor inventory of an {@link LivingEntity} as an {@link IItemHandler} using {@link LivingEntity#getItemBySlot(EquipmentSlot)} and
 * {@link LivingEntity#setItemSlot(EquipmentSlot, ItemStack)}.
 */
public class EntityArmorInvWrapper extends EntityEquipmentInvWrapper
{
    public EntityArmorInvWrapper(final LivingEntity entity)
    {
        super(entity, EquipmentSlot.Type.ARMOR);
    }
}
