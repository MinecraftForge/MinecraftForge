package net.minecraftforge.ingredients.capability;

import net.minecraftforge.ingredients.IngredientSource;
import net.minecraftforge.ingredients.IngredientStack;

import javax.annotation.Nullable;

/**
 * Base {@link IIngredientSourceProperties} wrapper for {@link IngredientSource}
 * */
public class IngredientSourcePropertiesWrapper implements IIngredientSourceProperties
{
    protected IngredientSource source;

    public IngredientSourcePropertiesWrapper(IngredientSource ingredientSource)
    {
        source = ingredientSource;
    }

    @Nullable
    @Override
    public IngredientStack getContents()
    {
        IngredientStack stack = source.getIngredient();
        return stack == null ? null : stack.copy();
    }

    @Override
    public int getCapacity()
    {
        return source.getCapacity();
    }

    @Override
    public boolean canAdd()
    {
        return source.canAdd();
    }

    @Override
    public boolean canRemove()
    {
        return source.canRemove();
    }

    @Override
    public boolean canAddIngredientType(IngredientStack stack)
    {
        return source.canAddIngredientType(stack);
    }

    @Override
    public boolean canRemoveIngredientType(IngredientStack stack)
    {
        return source.canRemoveIngredientType(stack);
    }
}
