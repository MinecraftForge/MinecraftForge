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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Class for building a brewing recipe. Takes an ingredients as input, an ingredient as ingredient and an itemstack as output.
 */
public class BrewingRecipeBuilder
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Ingredient input;
    private final Ingredient ingredient;
    private final ItemStack output;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private final IRecipeSerializer<?> type;

    public BrewingRecipeBuilder(IRecipeSerializer<?> type, Ingredient input, Ingredient ingredient, ItemStack output)
    {
        this.type = type;
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }

    public static BrewingRecipeBuilder brewing(Ingredient input, Ingredient ingredient, ItemStack output)
    {
        return new BrewingRecipeBuilder(BrewingRecipe.SERIALIZER, input, ingredient, output);
    }

    public BrewingRecipeBuilder unlocks(String key, ICriterionInstance criterion)
    {
        this.advancement.addCriterion(key, criterion);
        return this;
    }

    public void save(Consumer<IFinishedRecipe> recipe, String key)
    {
        this.save(recipe, new ResourceLocation(key));
    }

    public void save(Consumer<IFinishedRecipe> recipe, ResourceLocation location)
    {
        this.ensureValid(location);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(location)).rewards(AdvancementRewards.Builder.recipe(location)).requirements(IRequirementsStrategy.OR);
        recipe.accept(new BrewingRecipeBuilder.Result(location, this.type, this.input, this.ingredient, this.output, this.advancement, new ResourceLocation(location.getNamespace(), "recipes/" + this.output.getItem().getItemCategory().getRecipeFolderName() + "/" + location.getPath())));
    }

    private void ensureValid(ResourceLocation location)
    {
        if (this.advancement.getCriteria().isEmpty())
        {
            throw new IllegalStateException("No way of obtaining recipe " + location);
        }
    }

    public static class Result implements IFinishedRecipe
    {
        private final ResourceLocation id;
        private final Ingredient input;
        private final Ingredient ingredient;
        private final ItemStack output;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final IRecipeSerializer<?> type;

        public Result(ResourceLocation id, IRecipeSerializer<?> type, Ingredient input, Ingredient ingredient, ItemStack output, Advancement.Builder advancement, ResourceLocation advancementId)
        {
            this.id = id;
            this.type = type;
            this.input = input;
            this.ingredient = ingredient;
            this.output = output;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject tag)
        {
            tag.add("input", this.input.toJson());
            tag.add("ingredient", this.ingredient.toJson());
            JsonObject o = new JsonObject();
            o.addProperty("item", ForgeRegistries.ITEMS.getKey(this.output.getItem()).toString());
            o.addProperty("count", this.output.getCount());
            if (this.output.hasTag() && !this.output.getTag().isEmpty())
            {
                o.add("nbt", GSON.toJsonTree(this.output.getTag().toString()));
            }
            tag.add("output", o);
        }

        @Override
        public ResourceLocation getId()
        {
            return this.id;
        }

        @Override
        public IRecipeSerializer<?> getType()
        {
            return this.type;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement()
        {
            return this.advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId()
        {
            return this.advancementId;
        }
    }
}
