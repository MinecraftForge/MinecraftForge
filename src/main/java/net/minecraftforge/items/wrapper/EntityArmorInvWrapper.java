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

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

/**
 * Exposes the armor inventory of an {@link EntityLivingBase} as an {@link IItemHandler} using {@link EntityLivingBase#getItemStackFromSlot} and
 * {@link EntityLivingBase#setItemStackToSlot}.
 */
public class EntityArmorInvWrapper extends EntityEquipmentInvWrapper
{
    public EntityArmorInvWrapper(final EntityLivingBase entity)
    {
        super(entity, EntityEquipmentSlot.Type.ARMOR);
    }

    @Override
    public boolean isStackValidForSlot(@Nonnull ItemStack stack, int slot)
    {
        return stack.getItem().isValidArmor(stack, ItemHandlerHelper.armorSlots[slot], entity);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 1;
    }
}
