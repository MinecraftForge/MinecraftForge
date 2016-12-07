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

package net.minecraftforge.client.overlay;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * Handler for the the coloring of armor overlay.
 * */
public interface IArmorOverlayColor
{

    /**
     * Wrapper for vanilla. Always returns {@value defaultOverlayColor}
     * */
    IArmorOverlayColor VANILLA = new Vanilla();

    /**
     * Vanilla's default armor glint color value. Uses half the alpha value than the Item color.
     * */
    int defaultOverlayColor = 0xAA8040CC;

    /**
     * Used for coloring the first overlay pass.
     *
     * @param stack the ItemStack
     * @param wearer the Entity in question
     * @param slot the slot the armor is in
     *
     * @return a hex int (ARGB) used for coloring the effect
     * */
    int getFirstPassColor(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot);

    /**
     * Used for coloring the second overlay pass.
     *
     * @param stack the ItemStack
     * @param wearer the Entity in question
     * @param slot the slot the armor is in
     *
     * @return a hex int (ARGB) used for coloring the effect
     * */
    int getSecondPassColor(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot);

    final class Vanilla implements IArmorOverlayColor
    {

        @Override
        public int getFirstPassColor(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot)
        {
            return defaultOverlayColor;
        }

        @Override
        public int getSecondPassColor(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot)
        {
            return defaultOverlayColor;
        }

    }

}
