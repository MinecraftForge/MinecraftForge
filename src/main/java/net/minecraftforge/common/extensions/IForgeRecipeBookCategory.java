package net.minecraftforge.common.extensions;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;
import java.util.function.Predicate;

public interface IForgeRecipeBookCategory<T extends IRecipeType<?>> extends IForgeRegistryEntry<IForgeRecipeBookCategory<?>> {
    boolean isSearch();
    Predicate<IRecipe<?>> getPredicate();
    T getRecipeType();
    List<ItemStack> getIcons();
}
