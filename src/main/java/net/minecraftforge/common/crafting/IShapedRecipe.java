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

package net.minecraftforge.common.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Used to mark a recipe that shape matters so that the recipe
 * book and auto crafting picks the correct shape.
 * Note: These methods can't be named 'getHeight' or 'getWidth' due to obfusication issues.
 */
public interface IShapedRecipe<T extends Container> extends Recipe<T>
{
    int getRecipeWidth();
    int getRecipeHeight();
}
