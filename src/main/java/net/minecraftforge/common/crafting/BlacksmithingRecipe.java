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
import com.mojang.realmsclient.util.JsonUtils;

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

/**
 * A Recipe for the anvil. It takes 2 ingredients as input and returns an ItemStack.
 */
public class BlacksmithingRecipe implements IRecipe<IInventory>
{
    public static final IRecipeSerializer<BlacksmithingRecipe> SERIALIZER = new Serializer();
    
    private ResourceLocation id;
    private Ingredient base;
    private Ingredient addition;
    private ItemStack result;
    private int cost;
    
    public BlacksmithingRecipe(ResourceLocation id,Ingredient base, Ingredient addition, ItemStack result, int cost) 
    {
        this.id = id;
        this.base = base;
        this.addition = addition;
        this.result = result;
        this.cost = cost;
    }
    @Override
    public boolean matches(IInventory inventory, World level) 
    {
        return this.base.test(inventory.getItem(0)) && this.addition.test(inventory.getItem(1));
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
        return ForgeMod.BLACKSMITHING;
    }
    
    public int getCost() {
    	return this.cost;
    }
    
    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BlacksmithingRecipe> 
    {
	
        @Override
        public BlacksmithingRecipe fromJson(ResourceLocation id, JsonObject jsonobj) 
        {
            Ingredient base = Ingredient.fromJson(JSONUtils.getAsJsonObject(jsonobj, "base"));
            Ingredient addition = Ingredient.fromJson(JSONUtils.getAsJsonObject(jsonobj, "addition"));
            ItemStack result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(jsonobj, "result"));
            int cost = JsonUtils.getIntOr("cost", jsonobj, 0);
            return new BlacksmithingRecipe(id, base, addition, result, cost);
        }
        @Override
        public BlacksmithingRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) 
        {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            Ingredient ingredient1 = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            int cost = buffer.readInt();
            return new BlacksmithingRecipe(id, ingredient, ingredient1, result, cost);
        }
        @Override
        public void toNetwork(PacketBuffer buffer, BlacksmithingRecipe recipe) 
        {
            recipe.base.toNetwork(buffer);
            recipe.addition.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeInt(recipe.cost);
        }
    }
}
