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
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.RecipeType;

@SuppressWarnings("deprecation")
public abstract class ForgeRecipeManager implements IResourceManagerReloadListener
{
    protected final Map<RecipeType<? extends IRecipe>, List<? extends IRecipe>> sortedRecipes = Maps.newHashMap();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.sortedRecipes.clear();
        CraftingHelper.reloadConstants(resourceManager);
    }

    @SuppressWarnings("unchecked")
    public <T extends IRecipe> List<T> getRecipes(RecipeType<T> type)
    {
        return (List<T>) this.sortedRecipes.computeIfAbsent(type, t -> new ArrayList<>());
    }

    public ItemStack getResult(IInventory input, World world, RecipeType<?> type)
    {
        for(IRecipe irecipe : getRecipes(type))
            if (irecipe.matches(input, world)) return irecipe.getCraftingResult(input);
        return ItemStack.EMPTY;
    }

	@Nullable
    public <T extends IRecipe> T getRecipe(IInventory input, World world, RecipeType<T> type)
    {
        for(T irecipe : getRecipes(type))
            if (irecipe.matches(input, world)) return irecipe;
        return null;
    }

    /**
     * Modders should not use this if possible.  In the context that this is used, you should already have the recipe.  Do not run extra lookups using this method.
     */
    public NonNullList<ItemStack> getRemainingItems(IInventory input, World world, RecipeType<?> type)
    {
        for(IRecipe irecipe : getRecipes(type))
            if (irecipe.matches(input, world)) return irecipe.getRemainingItems(input);

        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(input.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i)
            nonnulllist.set(i, input.getStackInSlot(i));
        
        return nonnulllist;
    }

}
