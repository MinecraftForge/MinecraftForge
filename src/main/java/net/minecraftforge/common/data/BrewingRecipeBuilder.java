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
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.ContainerBrewingRecipe;
import net.minecraftforge.common.brewing.MixingBrewingRecipe;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Class for building a brewing recipe. Takes an ingredients as input, an ingredient as ingredient and an itemstack as output.
 */
public abstract class BrewingRecipeBuilder
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    protected final Advancement.Builder advancement = Advancement.Builder.advancement();
    protected final RecipeSerializer<?> type;

    public BrewingRecipeBuilder(RecipeSerializer<?> type)
    {
        this.type = type;
    }

    public static BrewingRecipeBuilder brewing(Ingredient input, Ingredient ingredient, ItemStack output)
    {
        return new NormalBrewingRecipeBuilder(input, ingredient, output);
    }

    public static ContainerBrewingRecipeBuilder container(Item input, Item ingredient, Item output)
    {
        return container(input, Ingredient.of(ingredient), output);
    }

    public static ContainerBrewingRecipeBuilder container(Item input, Ingredient ingredient, Item output)
    {
        return new ContainerBrewingRecipeBuilder(input, ingredient, output);
    }

    public static BrewingRecipeBuilder mixing(Potion input, Tag<Item> ingredient, Potion output)
    {
        return mixing(input, Ingredient.of(ingredient), output);
    }

    public static BrewingRecipeBuilder mixing(Potion input, Item ingredient, Potion output)
    {
        return mixing(input, Ingredient.of(ingredient), output);
    }

    public static BrewingRecipeBuilder mixing(Potion input, Ingredient ingredient, Potion output)
    {
        return new MixingBrewingRecipeBuilder(input, ingredient, output);
    }

    public <T extends BrewingRecipeBuilder> T unlocks(String key, CriterionTriggerInstance criterion)
    {
        this.advancement.addCriterion(key, criterion);
        return (T) this;
    }

    public void save(Consumer<FinishedRecipe> recipe, String key)
    {
        this.save(recipe, new ResourceLocation(key));
    }

    public void save(Consumer<FinishedRecipe> recipe, ResourceLocation location)
    {
        this.ensureValid(location);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(location)).rewards(AdvancementRewards.Builder.recipe(location)).requirements(RequirementsStrategy.OR);
        recipe.accept(build(location));
    }

    protected abstract FinishedRecipe build(ResourceLocation location);

    private void ensureValid(ResourceLocation location)
    {
        if (this.advancement.getCriteria().isEmpty())
        {
            throw new IllegalStateException("No way of obtaining recipe " + location);
        }
    }

    public static class NormalBrewingRecipeBuilder extends BrewingRecipeBuilder
    {
        private final Ingredient input;
        private final Ingredient ingredient;
        private final ItemStack output;

        public NormalBrewingRecipeBuilder(Ingredient input, Ingredient ingredient, ItemStack output)
        {
            super(BrewingRecipe.SERIALIZER);
            this.input = input;
            this.ingredient = ingredient;
            this.output = output;
        }

        @Override
        protected FinishedRecipe build(final ResourceLocation location)
        {
            return new Result(location, this.type, this.input, this.ingredient, this.output, this.advancement, new ResourceLocation(location.getNamespace(), "recipes/brewing/" + location.getPath()));
        }

        public record Result(ResourceLocation id,
                             RecipeSerializer<?> type,
                             Ingredient input,
                             Ingredient ingredient,
                             ItemStack output,
                             Advancement.Builder advancement,
                             ResourceLocation advancementId) implements FinishedRecipe
        {

            @Override
            public void serializeRecipeData(JsonObject tag)
            {
                tag.add("input", this.input.toJson());
                tag.add("ingredient", this.ingredient.toJson());
                JsonObject o = new JsonObject();
                o.addProperty("item", ForgeRegistries.ITEMS.getKey(this.output.getItem()).toString());
                o.addProperty("count", this.output.getCount());
                if (this.output.hasTag() && !this.output.getTag().isEmpty()) {
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
            public RecipeSerializer<?> getType()
            {
                return this.type;
            }

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

    public static class MixingBrewingRecipeBuilder extends BrewingRecipeBuilder
    {
        private final Potion input;
        private final Ingredient ingredient;
        private final Potion output;

        public MixingBrewingRecipeBuilder(Potion input, Ingredient ingredient, Potion output)
        {
            super(MixingBrewingRecipe.SERIALIZER);
            this.input = input;
            this.ingredient = ingredient;
            this.output = output;
        }

        @Override
        protected FinishedRecipe build(ResourceLocation location)
        {
            return new Result(location, this.type, this.input, this.ingredient, this.output, this.advancement, new ResourceLocation(location.getNamespace(), "recipes/brewing/" + location.getPath()));
        }

        public record Result(ResourceLocation id,
                             RecipeSerializer<?> type,
                             Potion input,
                             Ingredient ingredient,
                             Potion output,
                             Advancement.Builder advancement,
                             ResourceLocation advancementId) implements FinishedRecipe
        {

            @Override
            public void serializeRecipeData(JsonObject tag)
            {
                tag.addProperty("input_potion", ForgeRegistries.POTION_TYPES.getKey(this.input).toString());
                tag.add("ingredient", this.ingredient.toJson());
                tag.addProperty("output_potion", ForgeRegistries.POTION_TYPES.getKey(this.output).toString());
            }

            @Override
            public ResourceLocation getId()
            {
                return this.id;
            }

            @Override
            public RecipeSerializer<?> getType()
            {
                return this.type;
            }

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

    public static class ContainerBrewingRecipeBuilder extends BrewingRecipeBuilder
    {
        private final Item input;
        private final Ingredient ingredient;
        private final Item output;

        public ContainerBrewingRecipeBuilder(Item input, Ingredient ingredient, Item output)
        {
            super(ContainerBrewingRecipe.SERIALIZER);
            this.input = input;
            this.ingredient = ingredient;
            this.output = output;
        }

        @Override
        protected FinishedRecipe build(ResourceLocation location)
        {
            return new Result(location, this.type, this.input, this.ingredient, this.output, this.advancement, new ResourceLocation(location.getNamespace(), "recipes/brewing/" + location.getPath()));
        }

        public record Result(ResourceLocation id,
                             RecipeSerializer<?> type,
                             Item input,
                             Ingredient ingredient,
                             Item output,
                             Advancement.Builder advancement,
                             ResourceLocation advancementId) implements FinishedRecipe
        {

            @Override
            public void serializeRecipeData(JsonObject tag)
            {
                tag.addProperty("input_container", ForgeRegistries.ITEMS.getKey(this.input).toString());
                tag.add("ingredient", this.ingredient.toJson());
                tag.addProperty("output_container", ForgeRegistries.ITEMS.getKey(this.input).toString());
            }

            @Override
            public ResourceLocation getId()
            {
                return this.id;
            }

            @Override
            public RecipeSerializer<?> getType()
            {
                return this.type;
            }

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
}
