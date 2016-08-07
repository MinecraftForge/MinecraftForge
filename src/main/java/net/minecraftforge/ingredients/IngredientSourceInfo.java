package net.minecraftforge.ingredients;

import javax.annotation.Nullable;

/**
 * Wrapper class used to encapsulate the IIngredientSource's information
 * */
public final class IngredientSourceInfo
{
    @Nullable
    public final IngredientStack ingredient;
    public final int capacity;

    public IngredientSourceInfo(@Nullable IngredientStack ingredient, int capacity)
    {
        this.ingredient = ingredient;
        this.capacity = capacity;
    }

    public IngredientSourceInfo(IIngredientSource source)
    {
        this.ingredient = source.getIngredient();
        this.capacity = source.getCapacity();
    }
}
