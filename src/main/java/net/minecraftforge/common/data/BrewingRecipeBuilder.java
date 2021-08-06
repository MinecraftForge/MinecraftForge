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

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Class for building brewing recipes.
 */
public abstract class BrewingRecipeBuilder
{
    protected final RecipeSerializer<?> type;

    public BrewingRecipeBuilder(RecipeSerializer<?> type)
    {
        this.type = type;
    }

    /**
     * Create a {@link BrewingRecipeBuilder} for a normal brewing recipe
     * 
     * @param base    the {@link Ingredient} used as a brewing base (bottom slots)
     * @param reagent the {@link Ingredient} used as a brewing reagent (top slot)
     * @param result  the {@link ItemStack} result of the brewing recipe (replaces base after brewing)
     * @return the builder for a normal brewing recipe
     */
    public static BrewingRecipeBuilder brewing(Ingredient base, Ingredient reagent, ItemStack result)
    {
        return new NormalBrewingRecipeBuilder(base, reagent, result);
    }

    /**
     * Create a {@link BrewingRecipeBuilder} for a container brewing recipe
     *
     * @param base    the {@link Item} used as a container in the bottom slots
     * @param reagent the {@link Tag <Item>} used as a brewing reagent (top slot)
     * @param result  the {@link Item} result of the brewing recipe (replaces base after brewing, potions will be copied)
     * @return the builder for a container brewing recipe
     */
    public static BrewingRecipeBuilder container(Item base, Tag<Item> reagent, Item result)
    {
        return container(base, Ingredient.of(reagent), result);
    }

    /**
     * Create a {@link BrewingRecipeBuilder} for a container brewing recipe
     *
     * @param base    the {@link Item} used as a container in the bottom slots
     * @param reagent the {@link Item} used as a brewing reagent (top slot)
     * @param result  the {@link Item} result of the brewing recipe (replaces base after brewing, potions will be copied)
     * @return the builder for a container brewing recipe
     */
    public static BrewingRecipeBuilder container(Item base, Item reagent, Item result)
    {
        return container(base, Ingredient.of(reagent), result);
    }

    /**
     * Create a {@link BrewingRecipeBuilder} for a container brewing recipe
     *
     * @param base    the {@link Item} used as a container in the bottom slots
     * @param reagent the {@link Ingredient} used as a brewing reagent (top slot)
     * @param result  the {@link Item} result of the brewing recipe (replaces base after brewing, potions will be copied)
     * @return the builder for a container brewing recipe
     */
    public static BrewingRecipeBuilder container(Item base, Ingredient reagent, Item result)
    {
        return new ContainerBrewingRecipeBuilder(base, reagent, result);
    }

    /**
     * Create a {@link BrewingRecipeBuilder} for a mixing brewing recipe
     *
     * @param base    the {@link Potion} used as a base (bottom slots)
     * @param reagent the {@link Tag<Item>} used as a brewing reagent (top slot)
     * @param result  the {@link Potion} result of the brewing recipe (replaces base after brewing)
     * @return the builder for a mixing brewing recipe
     */
    public static BrewingRecipeBuilder mixing(Potion base, Tag<Item> reagent, Potion result)
    {
        return mixing(base, Ingredient.of(reagent), result);
    }

    /**
     * Create a {@link BrewingRecipeBuilder} for a mixing brewing recipe
     *
     * @param base    the {@link Potion} used as a base (bottom slots)
     * @param reagent the {@link Item} used as a brewing reagent (top slot)
     * @param result  the {@link Potion} result of the brewing recipe (replaces base after brewing)
     * @return the builder for a mixing brewing recipe
     */
    public static BrewingRecipeBuilder mixing(Potion base, Item reagent, Potion result)
    {
        return mixing(base, Ingredient.of(reagent), result);
    }

    /**
     * Create a {@link BrewingRecipeBuilder} for a mixing brewing recipe
     *
     * @param base    the {@link Potion} used as a base (bottom slots)
     * @param reagent the {@link Ingredient} used as a brewing reagent (top slot)
     * @param result  the {@link Potion} result of the brewing recipe (replaces base after brewing)
     * @return the builder for a mixing brewing recipe
     */
    public static BrewingRecipeBuilder mixing(Potion base, Ingredient reagent, Potion result)
    {
        return new MixingBrewingRecipeBuilder(base, reagent, result);
    }

    /**
     * Save the recipe with the given key
     *
     * @param recipe the consumer to which the built recipe will be passed
     * @param key    the key to save the recipe under
     */
    public void save(Consumer<FinishedRecipe> recipe, String key)
    {
        this.save(recipe, new ResourceLocation(key));
    }

    /**
     * Save the recipe with the given location/id
     *
     * @param recipe   the consumer to which the built recipe will be passed
     * @param location the location to save the recipe under
     */
    public void save(Consumer<FinishedRecipe> recipe, ResourceLocation location)
    {
        validate(location);
        recipe.accept(build(location));
    }

    protected abstract void validate(ResourceLocation id);

    /**
     * Build the recipe with the given location
     *
     * @param location the location/id of the recipe
     */
    protected abstract FinishedRecipe build(ResourceLocation location);

    public static class NormalBrewingRecipeBuilder extends BrewingRecipeBuilder
    {
        private final Ingredient base;
        private final Ingredient reagent;
        private final ItemStack result;

        public NormalBrewingRecipeBuilder(Ingredient base, Ingredient reagent, ItemStack result)
        {
            super(ForgeMod.BREWING_SERIALIZER.get());
            this.base = base;
            this.reagent = reagent;
            this.result = result;
        }

        @Override
        protected void validate(ResourceLocation id)
        {
            if (!ForgeRegistries.ITEMS.containsValue(this.result.getItem()))
                throw new IllegalArgumentException("Tried to use not registered item as result for "+id);
        }

        @Override
        protected FinishedRecipe build(final ResourceLocation location)
        {
            return new Result(location, this.type, this.base, this.reagent, this.result);
        }

        public record Result(ResourceLocation id,
                             RecipeSerializer<?> type,
                             Ingredient base,
                             Ingredient reagent,
                             ItemStack result) implements FinishedRecipe
        {

            @Override
            public void serializeRecipeData(JsonObject tag)
            {
                tag.add("base", this.base.toJson());
                tag.add("reagent", this.reagent.toJson());
                JsonObject o = new JsonObject();
                final ResourceLocation result = ForgeRegistries.ITEMS.getKey(this.result.getItem());
                o.addProperty("item", result.toString());
                o.addProperty("count", this.result.getCount());
                if (this.result.hasTag()) {
                    o.add("nbt", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, this.result.getTag()));
                }
                tag.add("result", o);
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
                return null;
            }

            @Nullable
            @Override
            public ResourceLocation getAdvancementId()
            {
                return null;
            }
        }
    }

    public static class MixingBrewingRecipeBuilder extends BrewingRecipeBuilder
    {
        private final Potion base;
        private final Ingredient reagent;
        private final Potion result;

        public MixingBrewingRecipeBuilder(Potion base, Ingredient reagent, Potion result)
        {
            super(ForgeMod.MIXING_BREWING_SERIALIZER.get());
            this.base = base;
            this.reagent = reagent;
            this.result = result;
        }

        @Override
        protected void validate(final ResourceLocation id)
        {
            if (!ForgeRegistries.POTIONS.containsValue(this.base))
                throw new IllegalArgumentException("Tried to use not registered potion as base for "+id);
            if (!ForgeRegistries.POTIONS.containsValue(this.result))
                throw new IllegalArgumentException("Tried to use not registered potion as result for "+id);
        }

        @Override
        protected FinishedRecipe build(ResourceLocation location)
        {
            return new Result(location, this.type, this.base, this.reagent, this.result);
        }

        public record Result(ResourceLocation id,
                             RecipeSerializer<?> type,
                             Potion base,
                             Ingredient reagent,
                             Potion result) implements FinishedRecipe
        {

            @Override
            public void serializeRecipeData(JsonObject tag)
            {
                final ResourceLocation base = ForgeRegistries.POTIONS.getKey(this.base);
                final ResourceLocation result = ForgeRegistries.POTIONS.getKey(this.result);
                tag.addProperty("base", base.toString());
                tag.add("reagent", this.reagent.toJson());
                tag.addProperty("result", result.toString());
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
                return null;
            }

            @Nullable
            @Override
            public ResourceLocation getAdvancementId()
            {
                return null;
            }
        }
    }

    public static class ContainerBrewingRecipeBuilder extends BrewingRecipeBuilder
    {
        private final Item base;
        private final Ingredient reagent;
        private final Item result;

        public ContainerBrewingRecipeBuilder(Item base, Ingredient reagent, Item result)
        {
            super(ForgeMod.CONTAINER_BREWING_SERIALIZER.get());
            this.base = base;
            this.reagent = reagent;
            this.result = result;
        }

        @Override
        protected void validate(final ResourceLocation id)
        {
            if (!ForgeRegistries.ITEMS.containsValue(this.base))
                throw new IllegalArgumentException("Tried to use not registered item as base for "+id);
            if (!ForgeRegistries.ITEMS.containsValue(this.result))
                throw new IllegalArgumentException("Tried to use not registered item as result for "+id);
        }

        @Override
        protected FinishedRecipe build(ResourceLocation location)
        {
            return new Result(location, this.type, this.base, this.reagent, this.result);
        }

        public record Result(ResourceLocation id,
                             RecipeSerializer<?> type,
                             Item base,
                             Ingredient reagent,
                             Item result) implements FinishedRecipe
        {

            @Override
            public void serializeRecipeData(JsonObject tag)
            {
                final ResourceLocation base = ForgeRegistries.ITEMS.getKey(this.base);
                final ResourceLocation result = ForgeRegistries.ITEMS.getKey(this.result);
                tag.addProperty("base", base.toString());
                tag.add("reagent", this.reagent.toJson());
                tag.addProperty("result", result.toString());
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
                return null;
            }

            @Nullable
            @Override
            public ResourceLocation getAdvancementId()
            {
                return null;
            }
        }
    }
}
