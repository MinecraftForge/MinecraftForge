/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;

/**
 * This interface is to be implemented on Container objects.
 * For GUIs with recipe books, this allows their containers to have
 * recipe completion and ghost recipes in their craft matrices.
 */
public interface IRecipeContainer
{
    /**
     * The crafting result slot of your container, where you take out the crafted item.
     * The equivalent for {@link net.minecraft.inventory.container.WorkbenchContainer} is {@link net.minecraft.inventory.container.WorkbenchContainer#field_75160_f}.
     * The equivalent for {@link net.minecraft.inventory.container.PlayerContainer} is {@link net.minecraft.inventory.container.PlayerContainer#field_75179_f}.
     */
    CraftResultInventory getCraftResult();

    /**
     * The crafting matrix of your container, where ingredients go for crafting.
     * The equivalent for {@link net.minecraft.inventory.container.WorkbenchContainer} is {@link net.minecraft.inventory.container.WorkbenchContainer#field_75162_e}.
     * The equivalent for {@link net.minecraft.inventory.container.PlayerContainer} is {@link net.minecraft.inventory.container.PlayerContainer#field_75181_e}.
     */
    CraftingInventory getCraftMatrix();
}
