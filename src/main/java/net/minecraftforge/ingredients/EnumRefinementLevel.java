package net.minecraftforge.ingredients;

/**
 * Used for defining the refinement level of a given IngredientStack
 * */
public enum EnumRefinementLevel
{
    /**
     * Base form of the ingredient.
     * */
    RAW,
    /**
     * 'Pure' variant of the ingredient.
     *
     * Think ore v. ingot.
     * */
    REFINED,
    /**
     * The recycled variant.
     *
     * */
    RECLAIMED;

    /**
     * Returns a refinement level based on its ordinal
     *
     * Defaults to RAW for non allowed values
     * */
    public static EnumRefinementLevel fromByte(byte id)
    {
        return id > -1 && id < values().length ? values()[id] : RAW;
    }
}
