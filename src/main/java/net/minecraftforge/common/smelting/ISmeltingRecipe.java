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

package net.minecraftforge.common.smelting;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public interface ISmeltingRecipe
{
    /**
     * @return a static {@link Ingredient} to be used as input
     */
    @Nonnull
    Ingredient getInput();

    /**
     * @return a {@link ItemStack} to be used as output <b>which has to be copied by the caller</b><br>
     * <br>
     * In case the passed input is {@link ItemStack#isEmpty()} a static default representation of the output has to be returned.<br>
     * The default state is used to keep binary compatibility with the vanilla system<br>
     */
    @Nonnull
    ItemStack getSmeltingResult(ItemStack input);

    default float getExperience(ItemStack input)
    {
        return input.getItem().getSmeltingExperience(input);
    }

    default ItemStack getDefaultResult()
    {
        return getSmeltingResult(ItemStack.EMPTY);
    }

    /**
     * @return if this recipe can be safely merged with another one if the inputs collide.
     * This typically means the output doesn't depend on the input<br>
     * Clarification on merging: {A,B}>R + {A,C}>R = {A,B,C}>R<br>
     */
    default boolean isBasic()
    {
        return false;
    }
}
