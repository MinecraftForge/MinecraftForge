package net.minecraftforge.common.brewing;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public abstract class AbstractBrewingRecipe<T> implements IBrewingRecipe {

    public final ItemStack input;
    public final T ingredient;
    public final ItemStack output;

    protected AbstractBrewingRecipe(ItemStack input, T ingredient, ItemStack output)
    {
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;

        if (this.input == null || this.ingredient == null || this.output == null)
        {
            throw new IllegalArgumentException("A brewing recipe cannot have a null parameter.");
        }
    }

    @Override
    public boolean isInput(ItemStack stack)
    {
        return OreDictionary.itemMatches(this.input, stack, false);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        return isInput(input) && isIngredient(ingredient) ? ItemStack.copyItemStack(output) : null;
    }
}