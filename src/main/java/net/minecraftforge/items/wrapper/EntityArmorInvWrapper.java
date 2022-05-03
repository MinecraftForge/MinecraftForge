/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.wrapper;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.items.IItemHandler;

/**
 * Exposes the armor inventory of an {@link EntityLivingBase} as an {@link IItemHandler} using {@link EntityLivingBase#getItemStackFromSlot} and
 * {@link EntityLivingBase#setItemStackToSlot}.
 */
public class EntityArmorInvWrapper extends EntityEquipmentInvWrapper
{
    public EntityArmorInvWrapper(final LivingEntity entity)
    {
        super(entity, EquipmentSlotType.Group.ARMOR);
    }
}
