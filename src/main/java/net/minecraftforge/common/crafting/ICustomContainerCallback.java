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

package net.minecraftforge.common.crafting;

import net.minecraft.item.ItemStack;

/**
 * Implements this interface on {@link net.minecraft.item.crafting.Ingredient Ingredient}
 * to enable override on default getContainerItem logic. Useful when fluid containers or
 * similar containers are involving into a recipe.
 *
 * @see net.minecraftforge.common.ForgeHooks#getContainerItem(ItemStack)
 */
public interface ICustomContainerCallback
{

    /**
     * Return a container item based on custom logic.
     *
     * @param input actual input ItemStack
     * @return Corresponding container item
     *
     * @see net.minecraft.item.Item#getContainerItem(ItemStack)
     * @see net.minecraftforge.common.ForgeHooks#getContainerItem(ItemStack)
     */
    ItemStack getContainer(ItemStack input);

}
