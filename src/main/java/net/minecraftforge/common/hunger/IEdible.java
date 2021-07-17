/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common.hunger;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Interface to be applied to non-standard food items or blocks that are edible, in order to obtain their food values.
 * For example, Forge implements this interface on {@link net.minecraft.block.CakeBlock}.
 * It is recommended to add a {@link net.minecraft.item.Food} object to your item and not use this interface.
 */
public interface IEdible
{
    /**
     * Obtain the FoodValues for this object
     * @param stack The ItemStack containing this edible object
     * @return The FoodValues for the object.
     */
    FoodValues getFoodValues(@Nonnull ItemStack stack);

    /**
     * @return true if this edible object can be eaten even when the player's hunger is full
     * @see net.minecraft.item.Food#canAlwaysEat()
     */
    boolean canAlwaysEat();
}
