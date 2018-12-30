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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.RecipeType;

@SuppressWarnings("deprecation")
public interface IForgeRecipeManager extends IResourceManagerReloadListener
{

    default <T extends IRecipe> Collection<T> getRecipes(RecipeType<T> type)
    {
        return recipesByType(type).values();
    }

    @Override
    default net.minecraftforge.resource.IResourceType getResourceType()
    {
        return net.minecraftforge.resource.VanillaResourceType.RECIPES;
    }

    <T extends IRecipe> Map<ResourceLocation, T> recipesByType(RecipeType<T> type);

    default Collection<IRecipe> getRecipes(Collection<RecipeType<?>> types)
    {
        Set<IRecipe> recipes = new HashSet<>();
        for(RecipeType<?> t : types) recipes.addAll(recipesByType(t).values());
        return recipes;
    }

}
