/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common.crafting;

import com.google.gson.JsonObject;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class GrindstoneRecipe implements IRecipe<IInventory>
{
    public static final IRecipeSerializer<GrindstoneRecipe> SERIALIZER = new Serializer();
    
    private ResourceLocation id;
    private Ingredient ingredient;
    private Ingredient ingredient1;
    private ItemStack result;
    
    public GrindstoneRecipe(ResourceLocation id,Ingredient ingredient, Ingredient ingredient1, ItemStack result) 
    {
        this.id = id;
        this.ingredient = ingredient;
        this.ingredient1 = ingredient1;
        this.result = result;
    }
    @Override
    public boolean matches(IInventory inventory, World level) 
    {
        return this.ingredient.test(inventory.getItem(0)) && this.ingredient1.test(inventory.getItem(1));
    }
    @Override
    public ItemStack assemble(IInventory inventory) 
    {
        return this.result.copy();
    }  
    @Override
    public boolean canCraftInDimensions(int width, int heigth) 
    {
        return true;
    }
    @Override
    public ItemStack getResultItem() 
    {
        return this.result;
    }
    @Override
    public ResourceLocation getId() 
    {   
        return this.id;
    }
    @Override
    public IRecipeSerializer<?> getSerializer() 
    {
        return SERIALIZER;
    }
    @Override
    public IRecipeType<?> getType() 
    {
        return ForgeMod.GRINDING;
    }
    
    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<GrindstoneRecipe> 
    {
	
        @Override
	public GrindstoneRecipe fromJson(ResourceLocation id, JsonObject jsonobj) 
        {
            Ingredient ingredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(jsonobj, "base"));
            Ingredient ingredient1 = Ingredient.fromJson(JSONUtils.getAsJsonObject(jsonobj, "addition"));
            ItemStack result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(jsonobj, "result"));
            return new GrindstoneRecipe(id, ingredient, ingredient1, result);
        }
        @Override
        public GrindstoneRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) 
        {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            Ingredient ingredient1 = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            return new GrindstoneRecipe(id, ingredient, ingredient1, result);
        }
        @Override
        public void toNetwork(PacketBuffer buffer, GrindstoneRecipe recipe) 
        {
            recipe.ingredient.toNetwork(buffer);
            recipe.ingredient1.toNetwork(buffer);
            buffer.writeItem(recipe.result);
        }
    }
}
