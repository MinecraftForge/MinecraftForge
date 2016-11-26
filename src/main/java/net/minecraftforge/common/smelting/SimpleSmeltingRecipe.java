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
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * A smelting recipe that checks against a single input.
 */
public class SimpleSmeltingRecipe extends AbstractSmeltingRecipe implements SimpleOutputSmeltingRecipe
{

    protected final ItemStack input, output;

    public SimpleSmeltingRecipe(@Nonnull ItemStack input, @Nonnull ItemStack output, int duration)
    {
        super(duration);
        this.input = checkNotNull(input);
        this.output = checkNotNull(output);
    }

    @Override
    public boolean matches(@Nonnull ItemStack input)
    {
        return OreDictionary.itemMatches(this.input, input, false);
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

    /**
     * The ItemStack this recipe matches against.
     * @return the input stack
     */
    @Nonnull
    public ItemStack getInput()
    {
        return input.copy();
    }

    @Nonnull
    @Override
    public ItemStack getOutput()
    {
        return output.copy();
    }
}
