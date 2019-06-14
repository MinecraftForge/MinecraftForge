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

package net.minecraftforge.common.extensions;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.crafting.RecipeType;
import net.minecraftforge.common.crafting.VanillaRecipeTypes;

public interface IForgeRecipe {

    /**
     * Used when sorting this recipe into it's category during load.  This type determines what class the recipe must subclass.
     * @return The type of this recipe.
     */
    default RecipeType<? extends IRecipe> getType()
    {
        return VanillaRecipeTypes.CRAFTING;
    }

}
