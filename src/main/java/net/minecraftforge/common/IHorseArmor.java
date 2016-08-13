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

package net.minecraftforge.common;

import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.item.ItemStack;

/**
 * When implemented by an item, it will be allowed to function as a piece of
 * horse armor.
 */
public interface IHorseArmor 
{
    /**
     * Provides the HorseArmorType to use for this armor. Used to change the
     * protection value provided.
     * 
     * @param stack The ItemStack instance of the armor.
     * @return HorseArmorType The HorseArmorType to use for this armor.
     */
    HorseArmorType getArmorType(ItemStack stack);
}
