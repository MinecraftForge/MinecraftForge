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

package net.minecraftforge.common.data;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.crafting.GrindingRecipe;

/**
 * Class for building a grinding recipe. Takes 2 ingredients as crafting materials and a itemstack as result.
 */
public class GrindingRecipeBuilder
{
    private final Ingredient base;
    private final Ingredient addition;
    private final ItemStack result;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private final IRecipeSerializer<?> type;
    
    public GrindingRecipeBuilder(IRecipeSerializer<?> type, Ingredient base, Ingredient addition, ItemStack result)
    {
        this.type = type;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }
    
    public static GrindingRecipeBuilder grinding(Ingredient base, Ingredient addition, ItemStack result)
    {
        return new GrindingRecipeBuilder(GrindingRecipe.SERIALIZER, base, addition, result);
    }
    
    public GrindingRecipeBuilder unlocks(String key, ICriterionInstance criterion) 
    {
        this.advancement.addCriterion(key, criterion);
        return this;
    }
    
    public void save(Consumer<IFinishedRecipe> recipe, String modid, String key) 
    {
        this.save(recipe, new ResourceLocation(modid, key));
    }

    public void save(Consumer<IFinishedRecipe> recipe, ResourceLocation location) 
    {
        this.ensureValid(location);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(location)).rewards(AdvancementRewards.Builder.recipe(location)).requirements(IRequirementsStrategy.OR);
        recipe.accept(new GrindingRecipeBuilder.Result(location, this.type, this.base, this.addition, this.result, this.advancement, new ResourceLocation(location.getNamespace(), "recipes/" + this.result.getItem().getItemCategory().getRecipeFolderName() + "/" + location.getPath())));
    }

    private void ensureValid(ResourceLocation location) 
    {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + location);
        }
    }

    public static class Result implements IFinishedRecipe 
    {
    	private final ResourceLocation id;
    	private final Ingredient base;
    	private final Ingredient addition;
    	private final ItemStack result;
    	private final Advancement.Builder advancement;
    	private final ResourceLocation advancementId;
    	private final IRecipeSerializer<?> type;
    	
    	public Result(ResourceLocation id, IRecipeSerializer<?> type, Ingredient base, Ingredient addition, ItemStack result, Advancement.Builder advancement, ResourceLocation advancementId) {
    		this.id = id;
    		this.type = type;
    		this.base = base;
    		this.addition = addition;
    		this.result = result;
    		this.advancement = advancement;
    		this.advancementId = advancementId;
    	}
    	
    	public void serializeRecipeData(JsonObject tag) 
    	{
    		tag.add("base", this.base.toJson());
    		tag.add("addition", this.addition.toJson());
    		JsonObject jsonobject = new JsonObject();
    		jsonobject.addProperty("item", Registry.ITEM.getKey(this.result.getItem()).toString());
    		jsonobject.addProperty("count", this.result.getCount());
    		tag.add("result", jsonobject);
    	}
    	
    	public ResourceLocation getId() 
    	{
    		return this.id;
    	}
    	
    	public IRecipeSerializer<?> getType() 
    	{
    		return this.type;
    	}
    	
    	@Nullable
    	public JsonObject serializeAdvancement() 
    	{
    		return this.advancement.serializeToJson();
    	}
    	
    	@Nullable
    	public ResourceLocation getAdvancementId() 
    	{
    		return this.advancementId;
    	}
    }
}
