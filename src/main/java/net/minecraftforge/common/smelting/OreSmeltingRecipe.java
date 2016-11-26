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
import java.util.List;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * A smelting recipe akin to ShapedOreRecipe and ShapelessOreRecipe. The input is checked against an OreDictionary entry.
 */
public class OreSmeltingRecipe extends AbstractSmeltingRecipe
{

    protected final ItemStack output;
    protected final List<ItemStack> inputs;

    public OreSmeltingRecipe(String input, ItemStack output, int duration)
    {
        super(duration);
        this.inputs = OreDictionary.getOres(checkNotNull(input));
        this.output = checkNotNull(output);
    }

    @Override
    public boolean matches(@Nonnull ItemStack input)
    {
        for (ItemStack candidate : inputs)
        {
            if (OreDictionary.itemMatches(input, candidate, false))
            {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(@Nonnull ItemStack input)
    {
        return output.copy();
    }

    @Override
    public Collection<ItemStack> getPossibleOutputs()
    {
        return Collections.singleton(output);
    }
}
