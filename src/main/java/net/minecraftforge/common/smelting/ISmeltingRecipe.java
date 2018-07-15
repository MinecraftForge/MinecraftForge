package net.minecraftforge.common.smelting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public interface ISmeltingRecipe
{
    ItemStack getSmeltingResult(ItemStack input);

    float getExperience(ItemStack input);

    /**
     * @return a static representation of the ItemStack return by {@link ISmeltingRecipe#getSmeltingResult(ItemStack)}
     * to be used in {@link FurnaceRecipes#getSmeltingList()}
     */
    ItemStack getGenericOutput();
}
