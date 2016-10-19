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

public abstract class AbstractBrewingRecipe<T> implements IBrewingRecipe {

    private final ItemStack input;
    private final T ingredient;
    private final ItemStack output;

    protected AbstractBrewingRecipe(ItemStack input, T ingredient, ItemStack output)
    {
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;

        if (this.getInput() == null || this.getIngredient() == null || this.getOutput() == null)
        {
            throw new IllegalArgumentException("A brewing recipe cannot have a null parameter.");
        }
        
        if (this.getInput().getMaxStackSize() != 1)
        {
            throw new IllegalArgumentException("Inputs must have a max size of 1 just like water bottles. Brewing Stands override the input with the output when the brewing is done, items that stack would end up getting lost.");
        }
    }

    @Override
    public boolean isInput(ItemStack stack)
    {
        return OreDictionary.itemMatches(this.getInput(), stack, false);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        return isInput(input) && isIngredient(ingredient) ? ItemStack.copyItemStack(getOutput()) : null;
    }

    public ItemStack getInput()
    {
        return input;
    }

    public T getIngredient()
    {
        return ingredient;
    }

    public ItemStack getOutput()
    {
        return output;
    }
}