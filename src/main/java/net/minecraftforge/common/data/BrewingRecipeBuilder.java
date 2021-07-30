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
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
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
            if (ForgeRegistries.ITEMS.getKey(this.result.getItem()) == null)
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
                    o.add("nbt", GsonHelper.parse(this.result.getTag().toString()));
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
}
