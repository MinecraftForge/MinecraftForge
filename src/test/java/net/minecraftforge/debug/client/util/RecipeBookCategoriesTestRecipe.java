package net.minecraftforge.debug.client.util;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Objects;

public class RecipeBookCategoriesTestRecipe extends ShapedRecipe {
    public static final IRecipeType<RecipeBookCategoriesTestRecipe> TYPE = new IRecipeType<RecipeBookCategoriesTestRecipe>() {};

    public RecipeBookCategoriesTestRecipe(ShapedRecipe recipe)
    {
        super(recipe.getId(), recipe.getGroup(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getIngredients(), recipe.getRecipeOutput());
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return TYPE;
    }

    @Nonnull
    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.ACACIA_LEAVES);
    }

    @Override
    public String toString() {
        return super.toString() + " output: {" + this.getRecipeOutput() + "}";
    }

    public static class Serializer extends ShapedRecipe.Serializer
    {
        @Override
        public @Nonnull
        RecipeBookCategoriesTestRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            return new RecipeBookCategoriesTestRecipe(super.read(recipeId, json));
        }

        @Override
        public RecipeBookCategoriesTestRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
            return new RecipeBookCategoriesTestRecipe(Objects.requireNonNull(super.read(recipeId, buffer)));
        }

        @Override
        public void write(@Nonnull PacketBuffer buffer, @Nonnull ShapedRecipe recipe) {
            super.write(buffer, recipe);
        }
    }
}
