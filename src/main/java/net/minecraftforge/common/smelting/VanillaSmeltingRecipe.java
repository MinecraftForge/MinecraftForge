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

package net.minecraftforge.common.smelting;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

final class VanillaSmeltingRecipe implements SmeltingRecipe
{

    private final ItemStack input, output;

    VanillaSmeltingRecipe(@Nonnull ItemStack input, @Nonnull ItemStack output)
    {
        this.input = checkNotNull(input);
        this.output = checkNotNull(output);
    }

    public final boolean matches(@Nonnull ItemStack input)
    {
        return OreDictionary.itemMatches(this.input, input, false);
    }

    @Nonnull
    @Override
    public final ItemStack getOutput(@Nonnull ItemStack input)
    {
        return matches(input) ? output.copy() : ItemStack.field_190927_a;
    }

    @Override
    public int getDuration(@Nonnull ItemStack input)
    {
        return SmeltingRecipeRegistry.DEFAULT_COOK_TIME;
    }

    @Override
    public Collection<Item> getPossibleOutputs()
    {
        return Collections.singleton(output.getItem());
    }
}
