package net.minecraft.src;

class EnumOptionsMappingHelper
{
    static final int[] enumOptionsMappingHelperArray = new int[EnumOptions.values().length];

    static
    {
        try
        {
            enumOptionsMappingHelperArray[EnumOptions.INVERT_MOUSE.ordinal()] = 1;
        }
        catch (NoSuchFieldError var6)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.VIEW_BOBBING.ordinal()] = 2;
        }
        catch (NoSuchFieldError var5)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.ANAGLYPH.ordinal()] = 3;
        }
        catch (NoSuchFieldError var4)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.ADVANCED_OPENGL.ordinal()] = 4;
        }
        catch (NoSuchFieldError var3)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.AMBIENT_OCCLUSION.ordinal()] = 5;
        }
        catch (NoSuchFieldError var2)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.RENDER_CLOUDS.ordinal()] = 6;
        }
        catch (NoSuchFieldError var1)
        {
            ;
        }
    }
}
