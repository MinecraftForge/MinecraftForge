package net.minecraftforge.recipe;

/**
 * This class represents ingredients used in recipes
 * 
 * @author Nephroid
 */
public abstract class AbstractIngredient implements Comparable
{
    public abstract boolean fulfilled(AbstractIngredient other);

    @Override
    public final boolean equals(Object other)
    {
        return this.compareTo(other) == 0;
    }
}