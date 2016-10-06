package net.minecraftforge.common.brewing;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class BrewingRecipe extends AbstractBrewingRecipe<ItemStack> {

    public BrewingRecipe(ItemStack input, ItemStack ingredient, ItemStack output)
    {
        super(input, ingredient, output);
    }

    @Override
    public boolean isIngredient(ItemStack stack)
    {
        return OreDictionary.itemMatches(this.ingredient, stack, false);
    }
}