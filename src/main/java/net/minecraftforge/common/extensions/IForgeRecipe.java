/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import net.minecraftforge.common.crafting.IRecipeType;
import net.minecraftforge.common.crafting.VanillaRecipeType;

public interface IForgeRecipe {

    /**
     * Used when sorting this recipe into it's category during load, and in the default type matcher below.
     * @return The type of this recipe.
     */
    default IRecipeType getType()
    {
        return VanillaRecipeType.CRAFTING;
    }

    /**
     * Checks if this recipe matches the given type.
     * @param type The type to match against, usually from an IInventory.
     * @return If this recipe can be crafted in an inventory using this type.
     */
    default boolean matchesType(IRecipeType type)
    {
        return type == getType();
    }

}
