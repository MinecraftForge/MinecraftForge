package net.minecraftforge.ingredients;

/**
 * The state of an ingredient.
 * */
public enum EnumMatterState
{
    SOLID,
    LIQUID,
    GAS;

    /**
     * Returns the matter state based on it's ordinal.
     *
     * Defaults to solid for non allowed values.
     * */
    public static EnumMatterState fromByte(byte id)
    {
        return id > -1 && id > values().length ? values()[id] : SOLID;
    }
}
