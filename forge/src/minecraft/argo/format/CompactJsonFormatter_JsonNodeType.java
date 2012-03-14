package argo.format;

import argo.jdom.JsonNodeType;

class CompactJsonFormatter_JsonNodeType
{
    /** A mapping helper array for EnumJsonNodeType's values. */
    static final int[] enumJsonNodeTypeMappingArray = new int[JsonNodeType.values().length];

    static
    {
        try
        {
            enumJsonNodeTypeMappingArray[JsonNodeType.ARRAY.ordinal()] = 1;
        }
        catch (NoSuchFieldError var7)
        {
            ;
        }

        try
        {
            enumJsonNodeTypeMappingArray[JsonNodeType.OBJECT.ordinal()] = 2;
        }
        catch (NoSuchFieldError var6)
        {
            ;
        }

        try
        {
            enumJsonNodeTypeMappingArray[JsonNodeType.STRING.ordinal()] = 3;
        }
        catch (NoSuchFieldError var5)
        {
            ;
        }

        try
        {
            enumJsonNodeTypeMappingArray[JsonNodeType.NUMBER.ordinal()] = 4;
        }
        catch (NoSuchFieldError var4)
        {
            ;
        }

        try
        {
            enumJsonNodeTypeMappingArray[JsonNodeType.FALSE.ordinal()] = 5;
        }
        catch (NoSuchFieldError var3)
        {
            ;
        }

        try
        {
            enumJsonNodeTypeMappingArray[JsonNodeType.TRUE.ordinal()] = 6;
        }
        catch (NoSuchFieldError var2)
        {
            ;
        }

        try
        {
            enumJsonNodeTypeMappingArray[JsonNodeType.NULL.ordinal()] = 7;
        }
        catch (NoSuchFieldError var1)
        {
            ;
        }
    }
}
