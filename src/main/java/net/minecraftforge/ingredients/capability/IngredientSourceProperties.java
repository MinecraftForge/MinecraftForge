package net.minecraftforge.ingredients.capability;

import net.minecraftforge.ingredients.IngredientSourceInfo;
import net.minecraftforge.ingredients.IngredientStack;

import javax.annotation.Nullable;

/**
 * Basic implementation of {@link IIngredientSourceProperties}
 * */
public class IngredientSourceProperties implements IIngredientSourceProperties
{
    @Nullable
    private final IngredientStack contents;
    private final int capacity;
    private final boolean canAdd;
    private final boolean canRemove;

    public IngredientSourceProperties(@Nullable IngredientStack contents, int capacity, boolean canAdd, boolean canRemove)
    {
        this.contents = contents;
        this.capacity = capacity;
        this.canAdd = canAdd;
        this.canRemove = canRemove;
    }

    @Nullable
    @Override
    public IngredientStack getContents()
    {
        return contents == null ? null : contents.copy();
    }

    @Override
    public int getCapacity()
    {
        return capacity;
    }

    @Override
    public boolean canAdd()
    {
        return canAdd;
    }

    @Override
    public boolean canRemove()
    {
        return canRemove;
    }

    @Override
    public boolean canAddIngredientType(IngredientStack stack)
    {
        return canAdd();
    }

    @Override
    public boolean canRemoveIngredientType(IngredientStack stack)
    {
        return canRemove();
    }

    /**
     * Converts the source info to source properties.
     *
     * @param mutable
     *          If true the properties will return true for {@link IIngredientSourceProperties#canAdd()}
     *          and {@link IngredientSourceProperties#canRemove()}
     * @param infos
     *          The {@link IngredientSourceInfo} to be converted.
     * @return An array of {@link IngredientSourceProperties}
     * */
    public static IngredientSourceProperties[] convert(boolean mutable, IngredientSourceInfo... infos)
    {
        IngredientSourceProperties[] props = new IngredientSourceProperties[infos.length];
        for(int i = 0; i < props.length; i++)
        {
            IngredientSourceInfo info = infos[i];
            props[i] = new IngredientSourceProperties(info.ingredient, info.capacity, mutable, mutable);
        }
        return props;
    }

}
