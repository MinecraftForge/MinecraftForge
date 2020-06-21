package net.minecraftforge.common.extensions;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;
import java.util.function.Predicate;

public interface IForgeRecipeBookCategory<T extends IRecipeType<?>> extends IForgeRegistryEntry<IForgeRecipeBookCategory<?>> {
    /**
     * If tab is search - does not filter recipes and always is on most top position
     * @return if tab is search
     */
    boolean isSearch();

    /**
     * Filter for displayed recipes
     * @return filtering function
     */
    Predicate<IRecipe<?>> getPredicate();

    /**
     * Recipe type of recipe book category
     * @return recipe type
     */
    T getRecipeType();

    /**
     * Recipe tab icons, can be 1 or 2 ItemStack
     * @return recipe tab icons
     */
    List<ItemStack> getIcon();
}
