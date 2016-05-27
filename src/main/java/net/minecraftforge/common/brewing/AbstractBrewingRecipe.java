package net.minecraftforge.common.brewing;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public abstract class AbstractBrewingRecipe<T> implements IBrewingRecipe {

    private final ItemStack input;
    private final T ingredient;
    private final ItemStack output;

    protected AbstractBrewingRecipe(ItemStack input, T ingredient, ItemStack output)
    {
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;

        if (this.getInput() == null || this.getIngredient() == null || this.getOutput() == null)
        {
            throw new IllegalArgumentException("A brewing recipe cannot have a null parameter.");
        }
        
        if (this.getInput().getMaxStackSize() != 1)
        {
            throw new IllegalArgumentException("Inputs must have a max size of 1 just like water bottles. Brewing Stands override the input with the output when the brewing is done, items that stack would end up getting lost.");
        }
    }

    @Override
    public boolean isInput(ItemStack stack)
    {
        return OreDictionary.itemMatches(this.getInput(), stack, false);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        return isInput(input) && isIngredient(ingredient) ? ItemStack.copyItemStack(getOutput()) : null;
    }

    public ItemStack getInput()
    {
        return input;
    }

    public T getIngredient()
    {
        return ingredient;
    }

    public ItemStack getOutput()
    {
        return output;
    }
}