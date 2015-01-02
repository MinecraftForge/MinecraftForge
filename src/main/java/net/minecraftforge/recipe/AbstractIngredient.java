package net.minecraftforge.recipe;

public abstract class AbstractIngredient implements Comparable
{
    public abstract boolean fulfilled(AbstractIngredient other);

    @Override
    public final boolean equals(Object other)
    {
        return this.compareTo(other) == 0;
    }
}