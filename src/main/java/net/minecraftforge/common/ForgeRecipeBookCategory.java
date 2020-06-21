package net.minecraftforge.common;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.common.extensions.IForgeRecipeBookCategory;

import java.util.List;
import java.util.function.Predicate;

public class ForgeRecipeBookCategory<T extends IRecipeType<?>> extends net.minecraftforge.registries.ForgeRegistryEntry<IForgeRecipeBookCategory<?>> implements IForgeRecipeBookCategory<T> {
    private final List<ItemStack> icons;
    private final boolean isSearch;
    private final T recipeType;
    private final Predicate<IRecipe<?>> predicate;

    /**
     * @param isSearch If tab is search
     * @param recipeType Recipe type
     * @param icons Recipe tab icons
     */
    public ForgeRecipeBookCategory(boolean isSearch, T recipeType, ItemStack... icons) {
        this(isSearch, recipeType, recipe -> true, icons);
    }

    /**
     * @param isSearch If tab is search
     * @param recipeType Recipe type
     * @param predicate Filtering function
     * @param icons Recipe tab icons
     */
    public ForgeRecipeBookCategory(boolean isSearch, T recipeType, Predicate<IRecipe<?>> predicate, ItemStack... icons) {
        this.isSearch = isSearch;
        this.recipeType = recipeType;
        this.predicate = predicate;
        this.icons = ImmutableList.copyOf(icons);
    }

    @Override
    public boolean isSearch() {
        return this.isSearch;
    }

    @Override
    public Predicate<IRecipe<?>> getPredicate() {
        return this.predicate;
    }

    @Override
    public T getRecipeType() {
        return this.recipeType;
    }

    @Override
    public List<ItemStack> getIcon() {
        return this.icons;
    }

    public static List<IForgeRecipeBookCategory<?>> getValidCategories(IRecipe<?> recipe) {
        return net.minecraftforge.registries.ForgeRegistries.RECIPE_BOOK_CATEGORIES.getValues().stream().filter(category -> {
            return recipe.getType().equals(category.getRecipeType()) && category.getPredicate().test(recipe);
        }).collect(java.util.stream.Collectors.toList());
    }
}
