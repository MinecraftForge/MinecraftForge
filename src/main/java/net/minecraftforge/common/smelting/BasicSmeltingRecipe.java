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

public final class BasicSmeltingRecipe implements ISmeltingRecipe
{
    private final Ingredient input;
    private final ItemStack output;
    private final float experience;

    public BasicSmeltingRecipe(Ingredient input, ItemStack output, float experience)
    {
        this.output = output;
        this.experience = experience;
        this.input = input;
    }

    public BasicSmeltingRecipe(ItemStack input, ItemStack output, float experience)
    {
        this(Ingredient.fromStacks(input), output, experience);
    }

    @Nonnull
    @Override
    public Ingredient getInput()
    {
        return input;
    }

    @Nonnull
    @Override
    public ItemStack getSmeltingResult(ItemStack input)
    {
        return output;
    }

    @Override
    public float getExperience(ItemStack input)
    {
        float ret = input.getItem().getSmeltingExperience(input);
        if (ret != -1) return ret;
        return experience;
    }

    @Override
    public boolean isBasic()
    {
        return true;
    }
}
