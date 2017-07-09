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

package net.minecraftforge.common.brewing;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;

public abstract class AbstractBrewingRecipe<T> implements IBrewingRecipe
{
    @Nonnull
    private final ItemStack input;
    private final T ingredient;
    private final ItemStack output;

    protected AbstractBrewingRecipe(@Nonnull ItemStack input, @Nonnull T ingredient, @Nonnull ItemStack output)
    {
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean isInput(@Nonnull ItemStack stack)
    {
        return OreDictionary.itemMatches(this.getInput(), stack, false);
    }

    @Override
    @Nonnull
    public ItemStack getOutput(@Nonnull ItemStack input, @Nonnull ItemStack ingredient)
    {
        return isInput(input) && isIngredient(ingredient) ? getOutput().copy() : ItemStack.EMPTY;
    }

    @Nonnull
    public ItemStack getInput()
    {
        return input;
    }

    @Nonnull
    public T getIngredient()
    {
        return ingredient;
    }

    @Nonnull
    public ItemStack getOutput()
    {
        return output;
    }
}