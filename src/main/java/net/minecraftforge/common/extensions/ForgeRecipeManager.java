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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.common.crafting.RecipeType;

@SuppressWarnings("deprecation")
public abstract class ForgeRecipeManager implements IResourceManagerReloadListener
{
    protected final Map<RecipeType<? extends IRecipe>, List<? extends IRecipe>> sortedRecipes = Maps.newHashMap();

    public <T extends IRecipe> Collection<T> getRecipes(RecipeType<T> type)
    {
        return recipesByType(type);
    }

    @SuppressWarnings("unchecked")
    public <T extends IRecipe> List<T> recipesByType(net.minecraftforge.common.crafting.RecipeType<T> type)
    {
        return (List<T>) this.sortedRecipes.computeIfAbsent(type, t -> new ArrayList<>());
    }

    public Collection<IRecipe> getRecipes(Collection<RecipeType<?>> types)
    {
        Set<IRecipe> recipes = new HashSet<>();
        for(RecipeType<?> t : types) recipes.addAll(recipesByType(t));
        return recipes;
    }

}
